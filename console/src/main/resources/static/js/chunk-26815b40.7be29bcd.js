(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-26815b40"],{"0d19":function(t,e,n){"use strict";n.r(e);var i=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("base-component",{on:{listenToChildEvent:t.getLogFromChild}},[n("Breadcrumb",{style:{margin:"15px 0 15px 185px",position:"fixed"}},[n("BreadcrumbItem",{attrs:{to:"/home"}},[t._v("首页")]),n("BreadcrumbItem",{attrs:{to:"/monitor"}},[t._v("冲突日志")])],1),n("Content",{staticClass:"content",style:{padding:"10px",background:"#fff",margin:"50px 0 1px 185px",zIndex:"1"}},[n("div",{staticStyle:{padding:"1px 1px"}},[n("i-input",{style:{margin:"10px, 0, 0, 0",width:"200px"},attrs:{placeholder:"输入DAL Cluster"},on:{input:t.getLogs},model:{value:t.keyWord,callback:function(e){t.keyWord=e},expression:"keyWord"}},[n("Icon",{attrs:{slot:"prefix",type:"ios-search"},slot:"prefix"})],1),n("Table",{staticStyle:{"margin-top":"20px"},attrs:{columns:t.columns,data:t.records,stripe:"",size:"small"}}),n("div",{staticStyle:{"text-align":"center",margin:"16px 0"}},[n("Page",{attrs:{total:t.total,current:t.current,"show-sizer":"","show-elevator":"",transfer:!0},on:{"update:current":function(e){t.current=e},"on-change":t.getLogs,"on-page-size-change":t.handleChangeSize}})],1),n("Modal",{attrs:{title:"查看SQL",width:"1200px"},on:{"on-ok":t.showSqlOk,"on-cancel":t.showSqlCancel},model:{value:this.sqlStatementModal,callback:function(e){t.$set(this,"sqlStatementModal",e)},expression:"this.sqlStatementModal"}},[n("Form",[n("FormItem",[n("codemirror",{ref:"mycode",staticClass:"code",attrs:{value:t.curCode,options:t.cmOptions}})],1)],1)],1)],1)])],1)},o=[],r=n("e66c");n("0f7c"),n("a7be"),n("f6b6");n("8c33"),n("31c5"),n("ffda"),n("9b74"),n("991c");var s={name:"Monitor",data:function(){var t=this;return{code:"//按Ctrl键进行代码提示",addDc:!1,sqlStatementModal:!1,handleSqlConflictModal:!1,curCode:"",cmOptions:{value:"",mode:"text/x-mysql",theme:"ambiance",lineWrapping:!0,height:100,readOnly:!0,lineNumbers:!0},columns:[{title:"DAL Cluster",key:"clusterName",tooltip:!0},{title:"源mha集群",key:"srcMhaName",tooltip:!0},{title:"目标mha集群",key:"destMhaName",tooltip:!0},{title:"SQL执行时间",key:"sqlExecuteTime",width:200,align:"center",render:function(t,e){return t("div",Object(r["a"])(new Date(e.row.sqlExecuteTime),"yyyy-MM-dd hh:mm:ss"))}},{title:"执行结果",key:"lastResult",width:120,align:"center",render:function(t,e){var n=e.row,i="commit"===n.lastResult?"blue":"volcano";return t("Tag",{props:{color:i}},n.lastResult)}},{title:"操作",key:"sqlStatement",width:160,align:"center",render:function(e,n){return e("div",[e("Button",{props:{type:"info",size:"small"},style:{marginRight:"5px"},on:{click:function(){t.showSqlInfo(n.row.rawSqlList)}}},"查看"),e("Button",{props:{type:"success",size:"small"},style:{marginLeft:"5px"},on:{click:function(){t.handleSqlConflict(n.row.id)}}},"处理")])}}],records:[],loading:!1,appId:0,total:0,current:1,size:10,lastTime:0,keyWord:""}},computed:{},methods:{getLogs:function(){var t=this;this.axios.get("/api/drc/v1/logs/conflicts/"+this.current+"/"+this.size+"?keyWord="+this.keyWord).then((function(e){console.log("test data"),console.log(e),t.records=e.data.data.list,t.total=e.data.data.count}))},handleChangeSize:function(t){var e=this;this.size=t,this.$nextTick((function(){e.getLogs()}))},showSqlInfo:function(t){this.curCode=t,this.sqlStatementModal=!0},showSqlOk:function(){this.sqlStatementModal=!1,this.curCode=""},showSqlCancel:function(){this.sqlStatementModal=!1,this.curCode=""},handleSqlConflict:function(t){this.$router.push({name:"conflict",query:{id:t}})}},created:function(){this.getLogs()}},c=s,a=(n("b0a2"),n("2877")),l=Object(a["a"])(c,i,o,!1,null,null,null);e["default"]=l.exports},"14c3":function(t,e,n){var i=n("c6b6"),o=n("9263");t.exports=function(t,e){var n=t.exec;if("function"===typeof n){var r=n.call(t,e);if("object"!==typeof r)throw TypeError("RegExp exec method returned something other than an Object or null");return r}if("RegExp"!==i(t))throw TypeError("RegExp#exec called on incompatible receiver");return o.call(t,e)}},"25f0":function(t,e,n){"use strict";var i=n("6eeb"),o=n("825a"),r=n("d039"),s=n("ad6d"),c="toString",a=RegExp.prototype,l=a[c],u=r((function(){return"/a/b"!=l.call({source:"a",flags:"b"})})),h=l.name!=c;(u||h)&&i(RegExp.prototype,c,(function(){var t=o(this),e=String(t.source),n=t.flags,i=String(void 0===n&&t instanceof RegExp&&!("flags"in a)?s.call(t):n);return"/"+e+"/"+i}),{unsafe:!0})},"31c5":function(t,e,n){(function(t){t(n("56b3"))})((function(t){"use strict";var e="CodeMirror-activeline",n="CodeMirror-activeline-background",i="CodeMirror-activeline-gutter";function o(t){for(var o=0;o<t.state.activeLines.length;o++)t.removeLineClass(t.state.activeLines[o],"wrap",e),t.removeLineClass(t.state.activeLines[o],"background",n),t.removeLineClass(t.state.activeLines[o],"gutter",i)}function r(t,e){if(t.length!=e.length)return!1;for(var n=0;n<t.length;n++)if(t[n]!=e[n])return!1;return!0}function s(t,s){for(var c=[],a=0;a<s.length;a++){var l=s[a],u=t.getOption("styleActiveLine");if("object"==typeof u&&u.nonEmpty?l.anchor.line==l.head.line:l.empty()){var h=t.getLineHandleVisualStart(l.head.line);c[c.length-1]!=h&&c.push(h)}}r(t.state.activeLines,c)||t.operation((function(){o(t);for(var r=0;r<c.length;r++)t.addLineClass(c[r],"wrap",e),t.addLineClass(c[r],"background",n),t.addLineClass(c[r],"gutter",i);t.state.activeLines=c}))}function c(t,e){s(t,e.ranges)}t.defineOption("styleActiveLine",!1,(function(e,n,i){var r=i!=t.Init&&i;n!=r&&(r&&(e.off("beforeSelectionChange",c),o(e),delete e.state.activeLines),n&&(e.state.activeLines=[],s(e,e.listSelections()),e.on("beforeSelectionChange",c)))}))}))},"44e7":function(t,e,n){var i=n("861d"),o=n("c6b6"),r=n("b622"),s=r("match");t.exports=function(t){var e;return i(t)&&(void 0!==(e=t[s])?!!e:"RegExp"==o(t))}},"4d63":function(t,e,n){var i=n("83ab"),o=n("da84"),r=n("94ca"),s=n("7156"),c=n("9bf2").f,a=n("241c").f,l=n("44e7"),u=n("ad6d"),h=n("9f7f"),f=n("6eeb"),d=n("d039"),p=n("69f3").set,g=n("2626"),v=n("b622"),m=v("match"),x=o.RegExp,y=x.prototype,b=/a/g,w=/a/g,C=new x(b)!==b,k=h.UNSUPPORTED_Y,E=i&&r("RegExp",!C||k||d((function(){return w[m]=!1,x(b)!=b||x(w)==w||"/a/i"!=x(b,"i")})));if(E){var S=function(t,e){var n,i=this instanceof S,o=l(t),r=void 0===e;if(!i&&o&&t.constructor===S&&r)return t;C?o&&!r&&(t=t.source):t instanceof S&&(r&&(e=u.call(t)),t=t.source),k&&(n=!!e&&e.indexOf("y")>-1,n&&(e=e.replace(/y/g,"")));var c=s(C?new x(t,e):x(t,e),i?this:y,S);return k&&n&&p(c,{sticky:n}),c},A=function(t){t in S||c(S,t,{configurable:!0,get:function(){return x[t]},set:function(e){x[t]=e}})},L=a(x),T=0;while(L.length>T)A(L[T++]);y.constructor=S,S.prototype=y,f(o,"RegExp",S)}g("RegExp")},5319:function(t,e,n){"use strict";var i=n("d784"),o=n("825a"),r=n("7b0b"),s=n("50c4"),c=n("a691"),a=n("1d80"),l=n("8aa5"),u=n("14c3"),h=Math.max,f=Math.min,d=Math.floor,p=/\$([$&'`]|\d\d?|<[^>]*>)/g,g=/\$([$&'`]|\d\d?)/g,v=function(t){return void 0===t?t:String(t)};i("replace",2,(function(t,e,n,i){var m=i.REGEXP_REPLACE_SUBSTITUTES_UNDEFINED_CAPTURE,x=i.REPLACE_KEEPS_$0,y=m?"$":"$0";return[function(n,i){var o=a(this),r=void 0==n?void 0:n[t];return void 0!==r?r.call(n,o,i):e.call(String(o),n,i)},function(t,i){if(!m&&x||"string"===typeof i&&-1===i.indexOf(y)){var r=n(e,t,this,i);if(r.done)return r.value}var a=o(t),d=String(this),p="function"===typeof i;p||(i=String(i));var g=a.global;if(g){var w=a.unicode;a.lastIndex=0}var C=[];while(1){var k=u(a,d);if(null===k)break;if(C.push(k),!g)break;var E=String(k[0]);""===E&&(a.lastIndex=l(d,s(a.lastIndex),w))}for(var S="",A=0,L=0;L<C.length;L++){k=C[L];for(var T=String(k[0]),R=h(f(c(k.index),d.length),0),M=[],H=1;H<k.length;H++)M.push(v(k[H]));var I=k.groups;if(p){var O=[T].concat(M,R,d);void 0!==I&&O.push(I);var N=String(i.apply(void 0,O))}else N=b(T,d,R,M,I,i);R>=A&&(S+=d.slice(A,R)+N,A=R+T.length)}return S+d.slice(A)}];function b(t,n,i,o,s,c){var a=i+t.length,l=o.length,u=g;return void 0!==s&&(s=r(s),u=p),e.call(c,u,(function(e,r){var c;switch(r.charAt(0)){case"$":return"$";case"&":return t;case"`":return n.slice(0,i);case"'":return n.slice(a);case"<":c=s[r.slice(1,-1)];break;default:var u=+r;if(0===u)return e;if(u>l){var h=d(u/10);return 0===h?e:h<=l?void 0===o[h-1]?r.charAt(1):o[h-1]+r.charAt(1):e}c=o[u-1]}return void 0===c?"":c}))}}))},6547:function(t,e,n){var i=n("a691"),o=n("1d80"),r=function(t){return function(e,n){var r,s,c=String(o(e)),a=i(n),l=c.length;return a<0||a>=l?t?"":void 0:(r=c.charCodeAt(a),r<55296||r>56319||a+1===l||(s=c.charCodeAt(a+1))<56320||s>57343?t?c.charAt(a):r:t?c.slice(a,a+2):s-56320+(r-55296<<10)+65536)}};t.exports={codeAt:r(!1),charAt:r(!0)}},"66d3":function(t,e,n){},7156:function(t,e,n){var i=n("861d"),o=n("d2bb");t.exports=function(t,e,n){var r,s;return o&&"function"==typeof(r=e.constructor)&&r!==n&&i(s=r.prototype)&&s!==n.prototype&&o(t,s),t}},"8aa5":function(t,e,n){"use strict";var i=n("6547").charAt;t.exports=function(t,e,n){return e+(n?i(t,e).length:1)}},"8c33":function(t,e,n){(function(t){t(n("56b3"))})((function(t){var e=/MSIE \d/.test(navigator.userAgent)&&(null==document.documentMode||document.documentMode<8),n=t.Pos,i={"(":")>",")":"(<","[":"]>","]":"[<","{":"}>","}":"{<","<":">>",">":"<<"};function o(t){return t&&t.bracketRegex||/[(){}[\]]/}function r(t,e,r){var c=t.getLineHandle(e.line),a=e.ch-1,l=r&&r.afterCursor;null==l&&(l=/(^| )cm-fat-cursor($| )/.test(t.getWrapperElement().className));var u=o(r),h=!l&&a>=0&&u.test(c.text.charAt(a))&&i[c.text.charAt(a)]||u.test(c.text.charAt(a+1))&&i[c.text.charAt(++a)];if(!h)return null;var f=">"==h.charAt(1)?1:-1;if(r&&r.strict&&f>0!=(a==e.ch))return null;var d=t.getTokenTypeAt(n(e.line,a+1)),p=s(t,n(e.line,a+(f>0?1:0)),f,d||null,r);return null==p?null:{from:n(e.line,a),to:p&&p.pos,match:p&&p.ch==h.charAt(0),forward:f>0}}function s(t,e,r,s,c){for(var a=c&&c.maxScanLineLength||1e4,l=c&&c.maxScanLines||1e3,u=[],h=o(c),f=r>0?Math.min(e.line+l,t.lastLine()+1):Math.max(t.firstLine()-1,e.line-l),d=e.line;d!=f;d+=r){var p=t.getLine(d);if(p){var g=r>0?0:p.length-1,v=r>0?p.length:-1;if(!(p.length>a))for(d==e.line&&(g=e.ch-(r<0?1:0));g!=v;g+=r){var m=p.charAt(g);if(h.test(m)&&(void 0===s||t.getTokenTypeAt(n(d,g+1))==s)){var x=i[m];if(x&&">"==x.charAt(1)==r>0)u.push(m);else{if(!u.length)return{pos:n(d,g),ch:m};u.pop()}}}}}return d-r!=(r>0?t.lastLine():t.firstLine())&&null}function c(t,i,o){for(var s=t.state.matchBrackets.maxHighlightLineLength||1e3,c=[],a=t.listSelections(),l=0;l<a.length;l++){var u=a[l].empty()&&r(t,a[l].head,o);if(u&&t.getLine(u.from.line).length<=s){var h=u.match?"CodeMirror-matchingbracket":"CodeMirror-nonmatchingbracket";c.push(t.markText(u.from,n(u.from.line,u.from.ch+1),{className:h})),u.to&&t.getLine(u.to.line).length<=s&&c.push(t.markText(u.to,n(u.to.line,u.to.ch+1),{className:h}))}}if(c.length){e&&t.state.focused&&t.focus();var f=function(){t.operation((function(){for(var t=0;t<c.length;t++)c[t].clear()}))};if(!i)return f;setTimeout(f,800)}}function a(t){t.operation((function(){t.state.matchBrackets.currentlyHighlighted&&(t.state.matchBrackets.currentlyHighlighted(),t.state.matchBrackets.currentlyHighlighted=null),t.state.matchBrackets.currentlyHighlighted=c(t,!1,t.state.matchBrackets)}))}t.defineOption("matchBrackets",!1,(function(e,n,i){function o(t){t.state.matchBrackets&&t.state.matchBrackets.currentlyHighlighted&&(t.state.matchBrackets.currentlyHighlighted(),t.state.matchBrackets.currentlyHighlighted=null)}i&&i!=t.Init&&(e.off("cursorActivity",a),e.off("focus",a),e.off("blur",o),o(e)),n&&(e.state.matchBrackets="object"==typeof n?n:{},e.on("cursorActivity",a),e.on("focus",a),e.on("blur",o))})),t.defineExtension("matchBrackets",(function(){c(this,!0)})),t.defineExtension("findMatchingBracket",(function(t,e,n){return(n||"boolean"==typeof e)&&(n?(n.strict=e,e=n):e=e?{strict:!0}:null),r(this,t,e)})),t.defineExtension("scanForBracket",(function(t,e,n,i){return s(this,t,e,n,i)}))}))},9263:function(t,e,n){"use strict";var i=n("ad6d"),o=n("9f7f"),r=RegExp.prototype.exec,s=String.prototype.replace,c=r,a=function(){var t=/a/,e=/b*/g;return r.call(t,"a"),r.call(e,"a"),0!==t.lastIndex||0!==e.lastIndex}(),l=o.UNSUPPORTED_Y||o.BROKEN_CARET,u=void 0!==/()??/.exec("")[1],h=a||u||l;h&&(c=function(t){var e,n,o,c,h=this,f=l&&h.sticky,d=i.call(h),p=h.source,g=0,v=t;return f&&(d=d.replace("y",""),-1===d.indexOf("g")&&(d+="g"),v=String(t).slice(h.lastIndex),h.lastIndex>0&&(!h.multiline||h.multiline&&"\n"!==t[h.lastIndex-1])&&(p="(?: "+p+")",v=" "+v,g++),n=new RegExp("^(?:"+p+")",d)),u&&(n=new RegExp("^"+p+"$(?!\\s)",d)),a&&(e=h.lastIndex),o=r.call(f?n:h,v),f?o?(o.input=o.input.slice(g),o[0]=o[0].slice(g),o.index=h.lastIndex,h.lastIndex+=o[0].length):h.lastIndex=0:a&&o&&(h.lastIndex=h.global?o.index+o[0].length:e),u&&o&&o.length>1&&s.call(o[0],n,(function(){for(c=1;c<arguments.length-2;c++)void 0===arguments[c]&&(o[c]=void 0)})),o}),t.exports=c},"991c":function(t,e,n){(function(t){t(n("56b3"),n("ffda"))})((function(t){"use strict";var e,n,i,o,r={QUERY_DIV:";",ALIAS_KEYWORD:"AS"},s=t.Pos,c=t.cmpPos;function a(t){return"[object Array]"==Object.prototype.toString.call(t)}function l(e){var n=e.doc.modeOption;return"sql"===n&&(n="text/x-sql"),t.resolveMode(n).keywords}function u(e){var n=e.doc.modeOption;return"sql"===n&&(n="text/x-sql"),t.resolveMode(n).identifierQuote||"`"}function h(t){return"string"==typeof t?t:t.text}function f(t,e){return a(e)&&(e={columns:e}),e.text||(e.text=t),e}function d(t){var e={};if(a(t))for(var n=t.length-1;n>=0;n--){var i=t[n];e[h(i).toUpperCase()]=f(h(i),i)}else if(t)for(var o in t)e[o.toUpperCase()]=f(o,t[o]);return e}function p(t){return e[t.toUpperCase()]}function g(t){var e={};for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n]);return e}function v(t,e){var n=t.length,i=h(e).substr(0,n);return t.toUpperCase()===i.toUpperCase()}function m(t,e,n,i){if(a(n))for(var o=0;o<n.length;o++)v(e,n[o])&&t.push(i(n[o]));else for(var r in n)if(n.hasOwnProperty(r)){var s=n[r];s=s&&!0!==s?s.displayText?{text:s.text,displayText:s.displayText}:s.text:r,v(e,s)&&t.push(i(s))}}function x(t){"."==t.charAt(0)&&(t=t.substr(1));for(var e=t.split(o+o),n=0;n<e.length;n++)e[n]=e[n].replace(new RegExp(o,"g"),"");return e.join(o)}function y(t){for(var e=h(t).split("."),n=0;n<e.length;n++)e[n]=o+e[n].replace(new RegExp(o,"g"),o+o)+o;var i=e.join(".");return"string"==typeof t?i:(t=g(t),t.text=i,t)}function b(t,i,r,c){var a=!1,l=[],u=i.start,h=!0;while(h)h="."==i.string.charAt(0),a=a||i.string.charAt(0)==o,u=i.start,l.unshift(x(i.string)),i=c.getTokenAt(s(t.line,i.start)),"."==i.string&&(h=!0,i=c.getTokenAt(s(t.line,i.start)));var f=l.join(".");m(r,f,e,(function(t){return a?y(t):t})),m(r,f,n,(function(t){return a?y(t):t})),f=l.pop();var d=l.join("."),v=!1,b=d;if(!p(d)){var w=d;d=C(d,c),d!==w&&(v=!0)}var k=p(d);return k&&k.columns&&(k=k.columns),k&&m(r,f,k,(function(t){var e=d;return 1==v&&(e=b),"string"==typeof t?t=e+"."+t:(t=g(t),t.text=e+"."+t.text),a?y(t):t})),u}function w(t,e){for(var n=t.split(/\s+/),i=0;i<n.length;i++)n[i]&&e(n[i].replace(/[,;]/g,""))}function C(t,e){var n=e.doc,i=n.getValue(),o=t.toUpperCase(),a="",l="",u=[],h={start:s(0,0),end:s(e.lastLine(),e.getLineHandle(e.lastLine()).length)},f=i.indexOf(r.QUERY_DIV);while(-1!=f)u.push(n.posFromIndex(f)),f=i.indexOf(r.QUERY_DIV,f+1);u.unshift(s(0,0)),u.push(s(e.lastLine(),e.getLineHandle(e.lastLine()).text.length));for(var d=null,g=e.getCursor(),v=0;v<u.length;v++){if((null==d||c(g,d)>0)&&c(g,u[v])<=0){h={start:d,end:u[v]};break}d=u[v]}if(h.start){var m=n.getRange(h.start,h.end,!1);for(v=0;v<m.length;v++){var x=m[v];if(w(x,(function(t){var e=t.toUpperCase();e===o&&p(a)&&(l=a),e!==r.ALIAS_KEYWORD&&(a=t)})),l)break}}return l}t.registerHelper("hint","sql",(function(t,r){e=d(r&&r.tables);var c=r&&r.defaultTable,a=r&&r.disableKeywords;n=c&&p(c),i=l(t),o=u(t),c&&!n&&(n=C(c,t)),n=n||[],n.columns&&(n=n.columns);var h,f,g,v=t.getCursor(),x=[],y=t.getTokenAt(v);if(y.end>v.ch&&(y.end=v.ch,y.string=y.string.slice(0,v.ch-y.start)),y.string.match(/^[.`"\w@][\w$#]*$/g)?(g=y.string,h=y.start,f=y.end):(h=f=v.ch,g=""),"."==g.charAt(0)||g.charAt(0)==o)h=b(v,y,x,t);else{var w=function(t,e){return"object"===typeof t?t.className=e:t={text:t,className:e},t};m(x,g,n,(function(t){return w(t,"CodeMirror-hint-table CodeMirror-hint-default-table")})),m(x,g,e,(function(t){return w(t,"CodeMirror-hint-table")})),a||m(x,g,i,(function(t){return w(t.toUpperCase(),"CodeMirror-hint-keyword")}))}return{list:x,from:s(v.line,h),to:s(v.line,f)}}))}))},"9b74":function(t,e,n){(function(t){t(n("56b3"))})((function(t){"use strict";var e="CodeMirror-hint",n="CodeMirror-hint-active";function i(t,e){this.cm=t,this.options=e,this.widget=null,this.debounce=0,this.tick=0,this.startPos=this.cm.getCursor("start"),this.startLen=this.cm.getLine(this.startPos.line).length-this.cm.getSelection().length;var n=this;t.on("cursorActivity",this.activityFunc=function(){n.cursorActivity()})}t.showHint=function(t,e,n){if(!e)return t.showHint(n);n&&n.async&&(e.async=!0);var i={hint:e};if(n)for(var o in n)i[o]=n[o];return t.showHint(i)},t.defineExtension("showHint",(function(e){e=s(this,this.getCursor("start"),e);var n=this.listSelections();if(!(n.length>1)){if(this.somethingSelected()){if(!e.hint.supportsSelection)return;for(var o=0;o<n.length;o++)if(n[o].head.line!=n[o].anchor.line)return}this.state.completionActive&&this.state.completionActive.close();var r=this.state.completionActive=new i(this,e);r.options.hint&&(t.signal(this,"startCompletion",this),r.update(!0))}})),t.defineExtension("closeHint",(function(){this.state.completionActive&&this.state.completionActive.close()}));var o=window.requestAnimationFrame||function(t){return setTimeout(t,1e3/60)},r=window.cancelAnimationFrame||clearTimeout;function s(t,e,n){var i=t.options.hintOptions,o={};for(var r in p)o[r]=p[r];if(i)for(var r in i)void 0!==i[r]&&(o[r]=i[r]);if(n)for(var r in n)void 0!==n[r]&&(o[r]=n[r]);return o.hint.resolve&&(o.hint=o.hint.resolve(t,e)),o}function c(t){return"string"==typeof t?t:t.text}function a(t,e){var n={Up:function(){e.moveFocus(-1)},Down:function(){e.moveFocus(1)},PageUp:function(){e.moveFocus(1-e.menuSize(),!0)},PageDown:function(){e.moveFocus(e.menuSize()-1,!0)},Home:function(){e.setFocus(0)},End:function(){e.setFocus(e.length-1)},Enter:e.pick,Tab:e.pick,Esc:e.close},i=/Mac/.test(navigator.platform);i&&(n["Ctrl-P"]=function(){e.moveFocus(-1)},n["Ctrl-N"]=function(){e.moveFocus(1)});var o=t.options.customKeys,r=o?{}:n;function s(t,i){var o;o="string"!=typeof i?function(t){return i(t,e)}:n.hasOwnProperty(i)?n[i]:i,r[t]=o}if(o)for(var c in o)o.hasOwnProperty(c)&&s(c,o[c]);var a=t.options.extraKeys;if(a)for(var c in a)a.hasOwnProperty(c)&&s(c,a[c]);return r}function l(t,e){while(e&&e!=t){if("LI"===e.nodeName.toUpperCase()&&e.parentNode==t)return e;e=e.parentNode}}function u(i,o){this.completion=i,this.data=o,this.picked=!1;var r=this,s=i.cm,u=s.getInputField().ownerDocument,h=u.defaultView||u.parentWindow,f=this.hints=u.createElement("ul"),d=i.cm.options.theme;f.className="CodeMirror-hints "+d,this.selectedHint=o.selectedHint||0;for(var p=o.list,g=0;g<p.length;++g){var v=f.appendChild(u.createElement("li")),m=p[g],x=e+(g!=this.selectedHint?"":" "+n);null!=m.className&&(x=m.className+" "+x),v.className=x,m.render?m.render(v,o,m):v.appendChild(u.createTextNode(m.displayText||c(m))),v.hintId=g}var y=i.options.container||u.body,b=s.cursorCoords(i.options.alignWithWord?o.from:null),w=b.left,C=b.bottom,k=!0,E=0,S=0;if(y!==u.body){var A=-1!==["absolute","relative","fixed"].indexOf(h.getComputedStyle(y).position),L=A?y:y.offsetParent,T=L.getBoundingClientRect(),R=u.body.getBoundingClientRect();E=T.left-R.left-L.scrollLeft,S=T.top-R.top-L.scrollTop}f.style.left=w-E+"px",f.style.top=C-S+"px";var M=h.innerWidth||Math.max(u.body.offsetWidth,u.documentElement.offsetWidth),H=h.innerHeight||Math.max(u.body.offsetHeight,u.documentElement.offsetHeight);y.appendChild(f);var I=f.getBoundingClientRect(),O=I.bottom-H,N=f.scrollHeight>f.clientHeight+1,P=s.getScrollInfo();if(O>0){var U=I.bottom-I.top,B=b.top-(b.bottom-I.top);if(B-U>0)f.style.top=(C=b.top-U-S)+"px",k=!1;else if(U>H){f.style.height=H-5+"px",f.style.top=(C=b.bottom-I.top-S)+"px";var q=s.getCursor();o.from.ch!=q.ch&&(b=s.cursorCoords(q),f.style.left=(w=b.left-E)+"px",I=f.getBoundingClientRect())}}var $,_=I.right-M;if(_>0&&(I.right-I.left>M&&(f.style.width=M-5+"px",_-=I.right-I.left-M),f.style.left=(w=b.left-_-E)+"px"),N)for(var F=f.firstChild;F;F=F.nextSibling)F.style.paddingRight=s.display.nativeBarWidth+"px";(s.addKeyMap(this.keyMap=a(i,{moveFocus:function(t,e){r.changeActive(r.selectedHint+t,e)},setFocus:function(t){r.changeActive(t)},menuSize:function(){return r.screenAmount()},length:p.length,close:function(){i.close()},pick:function(){r.pick()},data:o})),i.options.closeOnUnfocus)&&(s.on("blur",this.onBlur=function(){$=setTimeout((function(){i.close()}),100)}),s.on("focus",this.onFocus=function(){clearTimeout($)}));return s.on("scroll",this.onScroll=function(){var t=s.getScrollInfo(),e=s.getWrapperElement().getBoundingClientRect(),n=C+P.top-t.top,o=n-(h.pageYOffset||(u.documentElement||u.body).scrollTop);if(k||(o+=f.offsetHeight),o<=e.top||o>=e.bottom)return i.close();f.style.top=n+"px",f.style.left=w+P.left-t.left+"px"}),t.on(f,"dblclick",(function(t){var e=l(f,t.target||t.srcElement);e&&null!=e.hintId&&(r.changeActive(e.hintId),r.pick())})),t.on(f,"click",(function(t){var e=l(f,t.target||t.srcElement);e&&null!=e.hintId&&(r.changeActive(e.hintId),i.options.completeOnSingleClick&&r.pick())})),t.on(f,"mousedown",(function(){setTimeout((function(){s.focus()}),20)})),this.scrollToActive(),t.signal(o,"select",p[this.selectedHint],f.childNodes[this.selectedHint]),!0}function h(t,e){if(!t.somethingSelected())return e;for(var n=[],i=0;i<e.length;i++)e[i].supportsSelection&&n.push(e[i]);return n}function f(t,e,n,i){if(t.async)t(e,i,n);else{var o=t(e,n);o&&o.then?o.then(i):i(o)}}function d(e,n){var i,o=e.getHelpers(n,"hint");if(o.length){var r=function(t,e,n){var i=h(t,o);function r(o){if(o==i.length)return e(null);f(i[o],t,n,(function(t){t&&t.list.length>0?e(t):r(o+1)}))}r(0)};return r.async=!0,r.supportsSelection=!0,r}return(i=e.getHelper(e.getCursor(),"hintWords"))?function(e){return t.hint.fromList(e,{words:i})}:t.hint.anyword?function(e,n){return t.hint.anyword(e,n)}:function(){}}i.prototype={close:function(){this.active()&&(this.cm.state.completionActive=null,this.tick=null,this.cm.off("cursorActivity",this.activityFunc),this.widget&&this.data&&t.signal(this.data,"close"),this.widget&&this.widget.close(),t.signal(this.cm,"endCompletion",this.cm))},active:function(){return this.cm.state.completionActive==this},pick:function(e,n){var i=e.list[n],o=this;this.cm.operation((function(){i.hint?i.hint(o.cm,e,i):o.cm.replaceRange(c(i),i.from||e.from,i.to||e.to,"complete"),t.signal(e,"pick",i),o.cm.scrollIntoView()})),this.close()},cursorActivity:function(){this.debounce&&(r(this.debounce),this.debounce=0);var t=this.startPos;this.data&&(t=this.data.from);var e=this.cm.getCursor(),n=this.cm.getLine(e.line);if(e.line!=this.startPos.line||n.length-e.ch!=this.startLen-this.startPos.ch||e.ch<t.ch||this.cm.somethingSelected()||!e.ch||this.options.closeCharacters.test(n.charAt(e.ch-1)))this.close();else{var i=this;this.debounce=o((function(){i.update()})),this.widget&&this.widget.disable()}},update:function(t){if(null!=this.tick){var e=this,n=++this.tick;f(this.options.hint,this.cm,this.options,(function(i){e.tick==n&&e.finishUpdate(i,t)}))}},finishUpdate:function(e,n){this.data&&t.signal(this.data,"update");var i=this.widget&&this.widget.picked||n&&this.options.completeSingle;this.widget&&this.widget.close(),this.data=e,e&&e.list.length&&(i&&1==e.list.length?this.pick(e,0):(this.widget=new u(this,e),t.signal(e,"shown")))}},u.prototype={close:function(){if(this.completion.widget==this){this.completion.widget=null,this.hints.parentNode.removeChild(this.hints),this.completion.cm.removeKeyMap(this.keyMap);var t=this.completion.cm;this.completion.options.closeOnUnfocus&&(t.off("blur",this.onBlur),t.off("focus",this.onFocus)),t.off("scroll",this.onScroll)}},disable:function(){this.completion.cm.removeKeyMap(this.keyMap);var t=this;this.keyMap={Enter:function(){t.picked=!0}},this.completion.cm.addKeyMap(this.keyMap)},pick:function(){this.completion.pick(this.data,this.selectedHint)},changeActive:function(e,i){if(e>=this.data.list.length?e=i?this.data.list.length-1:0:e<0&&(e=i?0:this.data.list.length-1),this.selectedHint!=e){var o=this.hints.childNodes[this.selectedHint];o&&(o.className=o.className.replace(" "+n,"")),o=this.hints.childNodes[this.selectedHint=e],o.className+=" "+n,this.scrollToActive(),t.signal(this.data,"select",this.data.list[this.selectedHint],o)}},scrollToActive:function(){var t=this.hints.childNodes[this.selectedHint],e=this.hints.firstChild;t.offsetTop<this.hints.scrollTop?this.hints.scrollTop=t.offsetTop-e.offsetTop:t.offsetTop+t.offsetHeight>this.hints.scrollTop+this.hints.clientHeight&&(this.hints.scrollTop=t.offsetTop+t.offsetHeight-this.hints.clientHeight+e.offsetTop)},screenAmount:function(){return Math.floor(this.hints.clientHeight/this.hints.firstChild.offsetHeight)||1}},t.registerHelper("hint","auto",{resolve:d}),t.registerHelper("hint","fromList",(function(e,n){var i,o=e.getCursor(),r=e.getTokenAt(o),s=t.Pos(o.line,r.start),c=o;r.start<o.ch&&/\w/.test(r.string.charAt(o.ch-r.start-1))?i=r.string.substr(0,o.ch-r.start):(i="",s=o);for(var a=[],l=0;l<n.words.length;l++){var u=n.words[l];u.slice(0,i.length)==i&&a.push(u)}if(a.length)return{list:a,from:s,to:c}})),t.commands.autocomplete=t.showHint;var p={hint:t.hint.auto,completeSingle:!0,alignWithWord:!0,closeCharacters:/[\s()\[\]{};:>,]/,closeOnUnfocus:!0,completeOnSingleClick:!0,container:null,customKeys:null,extraKeys:null};t.defineOption("hintOptions",null)}))},"9f7f":function(t,e,n){"use strict";var i=n("d039");function o(t,e){return RegExp(t,e)}e.UNSUPPORTED_Y=i((function(){var t=o("a","y");return t.lastIndex=2,null!=t.exec("abcd")})),e.BROKEN_CARET=i((function(){var t=o("^r","gy");return t.lastIndex=2,null!=t.exec("str")}))},ac1f:function(t,e,n){"use strict";var i=n("23e7"),o=n("9263");i({target:"RegExp",proto:!0,forced:/./.exec!==o},{exec:o})},ad6d:function(t,e,n){"use strict";var i=n("825a");t.exports=function(){var t=i(this),e="";return t.global&&(e+="g"),t.ignoreCase&&(e+="i"),t.multiline&&(e+="m"),t.dotAll&&(e+="s"),t.unicode&&(e+="u"),t.sticky&&(e+="y"),e}},b0a2:function(t,e,n){"use strict";n("66d3")},d784:function(t,e,n){"use strict";n("ac1f");var i=n("6eeb"),o=n("d039"),r=n("b622"),s=n("9263"),c=n("9112"),a=r("species"),l=!o((function(){var t=/./;return t.exec=function(){var t=[];return t.groups={a:"7"},t},"7"!=="".replace(t,"$<a>")})),u=function(){return"$0"==="a".replace(/./,"$0")}(),h=r("replace"),f=function(){return!!/./[h]&&""===/./[h]("a","$0")}(),d=!o((function(){var t=/(?:)/,e=t.exec;t.exec=function(){return e.apply(this,arguments)};var n="ab".split(t);return 2!==n.length||"a"!==n[0]||"b"!==n[1]}));t.exports=function(t,e,n,h){var p=r(t),g=!o((function(){var e={};return e[p]=function(){return 7},7!=""[t](e)})),v=g&&!o((function(){var e=!1,n=/a/;return"split"===t&&(n={},n.constructor={},n.constructor[a]=function(){return n},n.flags="",n[p]=/./[p]),n.exec=function(){return e=!0,null},n[p](""),!e}));if(!g||!v||"replace"===t&&(!l||!u||f)||"split"===t&&!d){var m=/./[p],x=n(p,""[t],(function(t,e,n,i,o){return e.exec===s?g&&!o?{done:!0,value:m.call(e,n,i)}:{done:!0,value:t.call(n,e,i)}:{done:!1}}),{REPLACE_KEEPS_$0:u,REGEXP_REPLACE_SUBSTITUTES_UNDEFINED_CAPTURE:f}),y=x[0],b=x[1];i(String.prototype,t,y),i(RegExp.prototype,p,2==e?function(t,e){return b.call(t,this,e)}:function(t){return b.call(t,this)})}h&&c(RegExp.prototype[p],"sham",!0)}},e66c:function(t,e,n){"use strict";n.d(e,"a",(function(){return i}));n("4d63"),n("ac1f"),n("25f0"),n("5319");function i(t,e){var n={"M+":t.getMonth()+1,"d+":t.getDate(),"h+":t.getHours(),"m+":t.getMinutes(),"s+":t.getSeconds(),"S+":t.getMilliseconds()};for(var i in/(y+)/.test(e)&&(e=e.replace(RegExp.$1,(t.getFullYear()+"").substr(4-RegExp.$1.length))),n)new RegExp("("+i+")").test(e)&&(e=e.replace(RegExp.$1,1===RegExp.$1.length?n[i]:("00"+n[i]).substr((""+n[i]).length)));return e}},f6b6:function(t,e,n){}}]);
//# sourceMappingURL=chunk-26815b40.7be29bcd.js.map