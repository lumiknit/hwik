/*
type Div = TitleDiv | ImageDiv | VideoDiv | ParagraphDiv;

interface TitleDiv {
  type: "title";
  text: string;
  level: number; // Recommend 1 to 3
}

interface ImageDiv {
  type: "image";
  url: string;
  alt: string;
}

interface VideoDiv {
  type: "video";
  url: string;
  alt: string;
}

interface ParagraphDiv {
  type: "p";
  content: Span[];
}

type Span = TextSpan | LinkSpan | CodeSpan;

interface SpanStyle {
  bold?: boolean;
  italic?: boolean;
  underline?: boolean;
  strikeThrough?: boolean;
  monospace?: boolean;
  fgColor?: string | null; // RGB (rrggbb) hex color for light mode, optional
  bgColor?: string | null; // RGB (rrggbb) hex color for light mode, optional
}

interface TextSpan {
  type: "text";
  content: string;
  style: SpanStyle;
}

interface LinkSpan {
  type: "link";
  content: string;
  url: string;
  style: SpanStyle;
}

interface CodeSpan {
  type: "code";
  content: string;
  language?: string | null;
  style: SpanStyle;
}
*/

/**
 * Converts a DOM structure to an article JSON Object.
 *
 * Title, Image, Video should be placed in a Div array.
 * For paragraphs or text content, use the list of Spans.
 * From the computed styles, you should guess the style of the text.
 *
 * TYPE: (root: HTMLElement) -> Div[]
 */
function $divsFromDOM(root) {
	let divs = [];

	function getSpanStyle(cs) {
		return {
			bold: cs.fontWeight === "bold" || parseInt(cs.fontWeight) >= 700,
			italic: cs.fontStyle === "italic",
			underline: cs.textDecorationLine === "underline",
			strikeThrough: cs.textDecorationLine === "line-through",
			monospace: cs.fontFamily.includes("monospace"),
		};
	}

	let spans = [];

	function pushSpan(span) {
		// If all styles are the same, just push text
		const l = spans.length - 1;
		if (
			l >= 0 &&
			spans[l].type === span.type &&
			!!spans[l].style.bold === !!span.style.bold &&
			!!spans[l].style.italic === !!span.style.italic &&
			!!spans[l].style.underline === !!span.style.underline &&
			!!spans[l].style.strikeThrough === !!span.style.strikeThrough &&
			spans[l].style.fgLight === span.style.fgLight &&
			spans[l].style.bgLight === span.style.bgLight
		) {
			spans[l].content += span.content; // Concatenate text content
			return;
		}
		spans.push(span);
	}

	function pushText(t) {
		// Find the last span
		const l = spans.length - 1;
		if (l < 0) return;
		const sl = spans[l];
		sl.content += t; // Concatenate text content
	}

	function pushNewline(n) {
		// Find the last span
		const l = spans.length - 1;
		if (l < 0) return;
		const sl = spans[l];
		// Find count of last newlines
		let e = sl.content.length;
		while (e > 0 && sl.content[e - 1] === "\n") e--;
		if (sl.content.length - e < n) {
			sl.content = sl.content.slice(0, e) + "\n".repeat(n);
		}
	}

	function flushSpan() {
		if (spans.length > 0) {
			spans[spans.length - 1].content = spans[spans.length - 1].content.trimEnd();
			divs.push({
				type: "p",
				content: spans,
			});
			spans = [];
		}
	}

	function traverse(node, ctx) {
	    if (!node) return;
		if (node.nodeType === Node.TEXT_NODE) {
			if (node.textContent.trim() === "") return; // Ignore empty text nodes
			const span = {
				type: "text",
				content: node.textContent,
				style: ctx.style,
			};
			if (ctx.href) {
				span.type = "link";
				span.url = ctx.href;
			}
			pushSpan(span);
			return;
		} else if (node.nodeType !== Node.ELEMENT_NODE) {
			return; // Ignore non-element nodes
		}

		const cs = window.getComputedStyle(node);

		const childCtx = {
			...ctx,
			style: getSpanStyle(cs),
		};

		switch (node.tagName.toLowerCase()) {
			case "h1":
			case "h2":
			case "h3": {
				flushSpan();
				divs.push({
					type: "title",
					text: node.innerText,
					level: parseInt(node.tagName[1]), // h1 -> 1, h2 -> 2, h3 -> 3
				});
				break;
			}
			case "img": {
				flushSpan();
				divs.push({
					type: "image",
					url: node.src,
					alt: node.alt || "",
				});
				break;
			}
			case "video": {
				flushSpan();
				divs.push({
					type: "video",
					url: node.src,
					alt: node.getAttribute("alt") || "",
				});
				break;
			}
			case "a": {
				childCtx.href = node.href;
				break;
			}
			case "code": {
				childCtx.monospace = true;
				break;
			}
			case "br": {
				pushText("\n");
				break;
			}
		}
		// Traverse children
		for (let child of node.childNodes) {
			traverse(child, childCtx);
		}

		if (!cs.display.startsWith("inline")) {
			pushNewline(2);
		}
	}

	traverse(root, {});
	flushSpan();

	return divs;
}
