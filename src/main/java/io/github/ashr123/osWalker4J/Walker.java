package io.github.ashr123.osWalker4J;


import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Walker
{
	private Walker()
	{
	}

	private static WalkResult prepareWalkResult(Path top)
	{
		return Stream.of(Objects.requireNonNull(top.toFile().listFiles()))
				.reduce(
						new WalkResult(top, new LinkedList<>(), new LinkedList<>()),
						(walkResult, file) ->
						{
							(file.isDirectory() ? walkResult.dirs() : walkResult.files()).add(file);
							return walkResult;
						},
						(walkResult1, walkResult2) -> new WalkResult(
								top,
								Stream.concat(
										walkResult1.dirs().stream(),
										walkResult2.dirs().stream()
								).toList(),
								Stream.concat(
										walkResult1.files().stream(),
										walkResult2.files().stream()
								).toList()
						) // not in use anyway in sequential stream
				);
	}

	private static Stream<WalkResult> walk(WalkResult top)
	{
		return Stream.concat(
				Stream.of(top),
				top.dirs().stream()
						.map(directory -> prepareWalkResult(top.root().resolve(Path.of(directory.getName()))))
						.flatMap(Walker::walk)
		);
	}

	public static Stream<WalkResult> walk(Path top)
	{
		if (!top.toFile().isDirectory())
			throw new IllegalArgumentException("'" + top + "' must be a directory");
		if (!top.toFile().exists())
			throw new IllegalArgumentException("'" + top + "' doesn't exist");
		return walk(prepareWalkResult(top));
	}

	public record WalkResult(Path root, List<File> dirs, List<File> files)
	{
		@Override
		public String toString()
		{
			return "WalkResult[" +
					"root=" + root +
					", dirs=" + dirs.stream().map(File::getName).toList() +
					", files=" + files.stream().map(File::getName).toList() +
					']';
		}
	}
}