package io.github.ashr123.osWalker4J;

import java.nio.file.Path;

class WalkerTest
{

	@org.junit.jupiter.api.Test
	void walk()
	{
		Walker.walk(Path.of("."))
				.forEach(System.out::println);
	}
}