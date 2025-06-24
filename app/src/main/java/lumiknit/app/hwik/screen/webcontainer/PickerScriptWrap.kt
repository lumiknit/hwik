package lumiknit.app.hwik.screen.webcontainer

import kotlinx.serialization.json.JsonObject

fun wrapPickerScript(src: String, state: JsonObject): String {
	val wrapped = """
function ${"$"}divsFromDOM(t){let e=[],n=[];function l(){n.length>0&&(n[n.length-1].content=n[n.length-1].content.trimEnd(),e.push({type:"p",content:n}),n=[])}return!function t(i,o){var r;if(!i)return;if(i.nodeType===Node.TEXT_NODE){if(""===i.textContent.trim())return;let s={type:"text",content:i.textContent,style:o.style};o.href&&(s.type="link",s.url=o.href),function t(e){let l=n.length-1;if(l>=0&&n[l].type===e.type&&!!n[l].style.bold==!!e.style.bold&&!!n[l].style.italic==!!e.style.italic&&!!n[l].style.underline==!!e.style.underline&&!!n[l].style.strikeThrough==!!e.style.strikeThrough&&n[l].style.fgLight===e.style.fgLight&&n[l].style.bgLight===e.style.bgLight){n[l].content+=e.content;return}n.push(e)}(s);return}if(i.nodeType!==Node.ELEMENT_NODE)return;let c=window.getComputedStyle(i),a={...o,style:{bold:"bold"===(r=c).fontWeight||parseInt(r.fontWeight)>=700,italic:"italic"===r.fontStyle,underline:"underline"===r.textDecorationLine,strikeThrough:"line-through"===r.textDecorationLine,monospace:r.fontFamily.includes("monospace")}};switch(i.tagName.toLowerCase()){case"h1":case"h2":case"h3":l(),e.push({type:"title",text:i.innerText,level:parseInt(i.tagName[1])});break;case"img":l(),e.push({type:"image",url:i.src,alt:i.alt||""});break;case"video":l(),e.push({type:"video",url:i.src,alt:i.getAttribute("alt")||""});break;case"a":a.href=i.href;break;case"code":a.monospace=!0;break;case"br":!function t(e){let l=n.length-1;if(l<0)return;let i=n[l];i.content+="\n"}("\n")}for(let h of i.childNodes)t(h,a);c.display.startsWith("inline")||function t(e){let l=n.length-1;if(l<0)return;let i=n[l],o=i.content.length;for(;o>0&&"\n"===i.content[o-1];)o--;i.content.length-o<e&&(i.content=i.content.slice(0,o)+"\n".repeat(e))}(2)}(t,{}),l(),e}
($=>(()=>{
 try {
	$src
 }catch(e){
  return {error: "Error in script: " + e.message};
 }
})()||$)($state)
	"""
	return wrapped
}
