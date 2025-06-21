package lumiknit.app.hwik

import lumiknit.app.hwik.core.CondPageReady
import lumiknit.app.hwik.core.CondWait
import lumiknit.app.hwik.core.Meta
import lumiknit.app.hwik.core.PickerScript
import lumiknit.app.hwik.core.PickerStep
import org.junit.Test

class SourceScriptTest {
	@Test
	fun testJSON() {
		val ss = PickerScript(
			id = "test",
			meta = Meta(
				name = "Test Script",
				description = "This is a test script.",
				author = "Author Name",
				version = "2025.0601.1",
			),
			articleList = mutableListOf(
				PickerStep(
					condList = mutableListOf(),
					code = "console.log('Hello, World!')"
				),
				PickerStep(
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