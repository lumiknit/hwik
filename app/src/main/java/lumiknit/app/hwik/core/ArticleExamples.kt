package lumiknit.app.hwik.core

import kotlinx.datetime.Instant

fun exampleArticle(): Article {
	return Article(
		meta = ArticleMeta(
			title = "Example Article Good Wow amazing",
			author = "Author Name",
			date = Instant.parse("2023-10-01T12:00:00Z"),
		),
		content = listOf<Div>(
			TitleDiv(text = "Introduction", level = 1),
			ParagraphDiv(
				content = listOf(
					TextSpan(content = "Welcome to the example article. This article is designed to showcase the structure of an article in the Hwik app."),
					LinkSpan(content = "Learn more", url = "https://example.com"),
				)
			),
			TitleDiv(text = "Section 1", level = 2),
			ParagraphDiv(
				content = listOf(
					TextSpan(content = "This is the first section of the article. It contains some introductory information."),
					LinkSpan(content = "Read more", url = "https://example.com/section1"),
				)
			),
			TitleDiv(text = "Section 2", level = 2),
			ImageDiv(
				url = "https://www.gstatic.com/devrel-devsite/prod/v7aeef7f1393bb1d75a4489145c511cdd5aeaa8e13ad0a83ec1b5b03612e66330/android/images/lockup.png",
				alt = "Example Image"
			),
			TitleDiv(text = "Section 3", level = 2),
			VideoDiv(
				url = "https://www.sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4",
				alt = "Example Video"
			),
			ParagraphDiv(
				content = listOf(
					TextSpan(
						content =
							"Long section. " +
									"This section contains a lot of text to demonstrate how the article layout handles longer content. " +
									"It includes multiple paragraphs, images, and links to provide a comprehensive example of an article structure in the Hwik app." +
									"Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
									"Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
									"Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
									"다람쥐 헌 쳇바퀴"
					),
					TextSpan(content = " and", style = SpanStyle(italic = true)),
					TextSpan(
						content = " more text to fill the space and demonstrate the layout capabilities of the Hwik app. " +
								"This is a continuation of the long section, ensuring that we have enough content to test the scrolling and layout features of the article view." +
								"Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
								"Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
								"Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat." +
								"다람쥐 헌 쳇바퀴"
					)
				)
			),
			TitleDiv(text = "End of contents", level = 1),
		)
	)
}