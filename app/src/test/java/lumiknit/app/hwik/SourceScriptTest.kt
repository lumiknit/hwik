package lumiknit.app.hwik

import lumiknit.app.hwik.core.Meta
import lumiknit.app.hwik.core.PickerProcess
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
			articleList = PickerProcess(
				mutableListOf(
					PickerStep(
						code = "console.log('Hello, World!')"
					),
					PickerStep(
						condWaitSeconds = 1.5,
						code = "alert('This is a test alert!')\nconsole.log('This is a test log!')"
					),
				)
			),
		)

		val src = ss.toPrettyJSON()

		// Log the src
		println(src)
	}
}