## PickerScript Grammar Details

PickerScript is designed to crawl web posts for the purpose of displaying them
in a better reader environment. Its basic structure follows JavaScript, but it
uses special syntax elements to define the crawling process.

### References

* For the concrete script structures,
  see [PickerScript.kt]().
* For the parser,
  see [PickerScriptParser.kt]().

### Features

The entire PickerScript is a valid JavaScript file. The script describes
metadata and processes, and separates scripts step by step.

### Syntax

PickerScript itself is Javascript. Line comments starting
with '/// <SYM> <REST>' are used as directives to give them special meaning.
Based on the <SYM> character, the interpretation will be different.

#### Directives

These are special forms of comments that convey specific meaning.

* `@`: Denotes metadata.
    * **Syntax:** `/// @ <key> <value>`
    * `<key>` is a single word, case-insensitive, and underlines are ignored.
    * `<value>` is the rest of the line, with the string trimmed.
    * Special metas
        * `id`: ID of the script, required.
        * `name`, `version`: Displayed name and version of the script.
        * `author`: Author of the script.
        * `description`: Description of the script.
        * `urlRE`: Regex for URL. For article list/search query, only urls that
          match this regex will be processed by the script.
* `*`: Process (pipeline) start.
    * **Syntax:** `/// * <kind>`
    * `kind` is case insensitive, ignores underlines. The defined kinds are:
        * `articleList`: Queries a list of articles. State will be kept, and the
          output is `urls` (string array).
        * `articleContent`: Takes a URL (`url`: string) and outputs article
          content (`article`: JSON object).
        * `search`: Takes a query string (`query`: string) and returns search
          result
          URLs (`urls`: string array).
* `-`: Step divider.
    * **Syntax:** `/// - [wait <seconds>]`
    * If `wait` and a number of seconds are specified, it will wait for the
      specified time before executing the next step.

#### Codes

You can place JavaScript code anywhere in the PickerScript file. However, some
rules exist.

* Each block of code will be placed in a process step.
* The code should be placed *after* the process start marker (`/// *`).
* The code will be separated into steps by step dividers (`/// -`). If there is
  no step divider before a code block, it will be the first step of that
  process.
* Each step runs in an async function, allowing you to use `await` for
  asynchronous operations.
* For each step, the 'input' and 'output' are passed via the variable `$`.
* It's basically an Object.
* You can set fields in the object `$` for output.
* You can return any object to replace the entire `$` object for the next step.
* Fields starting with `$` (`$*`) are temporary variables. They are removed
  after the step finishes.
* All other fields are kept during the process. For `articleList`, the state
  will be kept for future processes.

```javascript
/// * articleList
/// -
// First step: Fetch initial data from the input URL.
// Set or add necessary information to the '$' object.
$.$tempData = await fetch($.url).then(res => res.text());
$.extractedLinks = extractLinks($.$tempData); // Assuming extractLinks is a custom function

/// - wait 2
// Second step: Wait 2 seconds, then process the list of links.
// The temporary variable '$tempData' is not available here.
const processedUrls = $.extractedLinks.map(link => resolveUrl($.baseUrl, link));
// Set the result URL list to the '$urls' field for the next step.
$.$urls = processedUrls;

// The final output of this process is the string array in '$urls'.
```

### Special State Fields

Predefined special fields exist in the `$` object for data transfer and control
between processes.

* **Common (Available in the `$` object)**
* `$href: string`: Redirect to this URL after the end of the step.
* **`articleList` Process**
* `$urls: string[]`: The list of listed URLs. The content of this field becomes
  the final output of the process.
* **`articleContent` Process**
* `url: string`: The input value for the article URL.
* `$article: JSONObject`: The output value for the article content, which is a
  JSON object.
* **`search` Process**
* `query: string`: The input query string.
* `$urls: string[]`: The output URLs of the search result.

### Helpers

* `$domToDivs(HTMLElement): JSONObject`: A helper function to convert a DOM
  element into a JSON object. This allows easy extraction of structured content
  from web pages.

## Example

```javascript
/// @ id google_search
/// @ name Google Search
/// @ version 2025.0624.1
/// @ author Aleph
/// @ description Find google search results
/// @ urlre ^https?://www\.google\.com/search\?q=.*$

/// * articleList

/// -
// For the first step, just go to google search page
// '$' is a special variable that contains all state (including inputs, last step's outputs)
window.location.href = "https://www.google.com/search?q=" + $query;

/// - wait 1
// Just wait for 1 second

/// -
let urls = []
document.querySelectorAll("a").forEach((e) => {
urls.push(e.href)
});
$.urls = urls; // Save the URLs to the state

/// * search
...
