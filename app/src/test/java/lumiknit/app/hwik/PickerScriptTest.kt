package lumiknit.app.hwik

import lumiknit.app.hwik.core.Meta
import lumiknit.app.hwik.core.PickerProcess
import lumiknit.app.hwik.core.PickerScript
import lumiknit.app.hwik.core.PickerStep
import org.junit.Test

class PickerScriptTest {
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
		println("PickerScript JSON:\n$src")

		println("PickerScript Text:\n${ss.toText()}")
	}

	@Test
	fun testParse1() {
		val src1 = """
			/// @ id main
			/// @ name Hello, world!
			///@version 25.0601.1
			
			/// * Articlelist
			  console.log("First abc")
				
				// then, fetch
				fetch()
			  /// - wait 1.5
				void 0
			
			/// @description This is a test script.
			
			/// *Search
			  /// - wait 20
				  alert("BOOM")
				/// -
				  then
		""".trimIndent()
		val validate = { parsed: PickerScript ->
			assert(parsed.id == "main") { "Parsed ID should be 'main'" }
			assert(parsed.meta.name == "Hello, world!") { "Parsed name should be 'Hello, world!'" }
			assert(parsed.meta.version == "25.0601.1") { "Parsed version should be '25.0601.1'" }
			assert(parsed.meta.description == "This is a test script.") { "Parsed description should be 'This is a test script.'" }
			assert(parsed.articleList.steps.size == 2) { "Parsed articleList should have 2 steps" }
			assert(parsed.articleList.steps[0].code.contains("console.log(\"First abc\")")) { "First step code should be 'console.log(\"First abc\")'" }
			assert(parsed.articleList.steps[1].condWaitSeconds == 1.5) { "Second step should have condWaitSeconds of 1.5" }
			assert(parsed.search.steps.size == 2) { "Parsed search should have 2 steps" }
		}

		val parsed = PickerScript.fromText(src1)
		println("Parsed PickerScript:\n${parsed.toText()}")
		validate(parsed)

		// DUmp-parse
		val dumpedScript = parsed.toText()
		val dumpedJSON = parsed.toJSON()

		val p1 = PickerScript.fromText(dumpedScript)
		val p2 = PickerScript.fromText(dumpedJSON)

		// Deep compare
		validate(p1)
		validate(p2)
		assert(p1.equals(p2))
	}
}