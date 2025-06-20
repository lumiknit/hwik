package lumiknit.app.hwik

import lumiknit.app.hwik.core.CondPageReady
import lumiknit.app.hwik.core.CondWait
import lumiknit.app.hwik.core.Meta
import lumiknit.app.hwik.core.SourceScript
import lumiknit.app.hwik.core.Step
import org.junit.Test

class SourceScriptTest {
	@Test
	fun testJSON() {
		val ss = SourceScript(
			id = "test",
			meta = Meta(
				name = "Test Script",
				description = "This is a test script.",
				author = "Author Name",
				version = "2025.0601.1",
			),
			entryList = mutableListOf(
				Step(
					condList = mutableListOf(),
					code = "console.log('Hello, World!')"
				),
				Step(
					condList = mutableListOf(
						CondWait(seconds = 1.5),
						CondPageReady(),
					),
					code = "alert('This is a test alert!')\nconsole.log('This is a test log!')"
				),
			),
		)

		val src = ss.toPrettyJSON()

		// Log the src
		println(src)
	}
}