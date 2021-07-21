//reference: https://github.com/Scuilion/confluence-stash-macro/blob/master/stash-macro/src/main/resources/js/stash-macro.js
// https://community.developer.atlassian.com/t/documentation-for-ajs-macrobrowser-setmacrojsoverride/49746/2
// https://community.developer.atlassian.com/t/how-to-override-macrobrowser-postpreview-function/49942/3

(function($) {
    var MermaidMacro = function(){
    };
    var originalCode;

    //overrides the parameter field UI based on parameter type or parameter name
    MermaidMacro.prototype.fields = {
        "string" : function(param, options){
            if(param.name == "Code"){

                // Confluence.Templates.MacroBrowser.macroParameter() returns a String:
                // <div class="macro-param-div"><label></label><input type="text" class="text"/></div>
                // paramDiv creates the above elements
                var paramDiv = AJS.$(Confluence.Templates.MacroBrowser.macroParameter());
                

                // $(selector, context)
                // selector - A string containing a selector expression i.e. id of tag
                // context - A DOM Element, Document, jQuery or selector to use as context
                // create paramDiv with id="macro-para-div-Code"
                var input = AJS.$("macro-param-div-Code", paramDiv);

                var textArea = document.createElement("textarea");
                textArea.style.resize = "none";
                textArea.style.overflow = "scroll";
                textArea.setAttribute("rows", "12");
                textArea.setAttribute("cols", "30");
                textArea.setAttribute("id", "macro-param-Code");
                textArea.setAttribute("class", "macro-param-input");

                var label = document.createElement("label");
                label.innerHTML = "Code";
                label.setAttribute("for", "macro-param-Code");

                paramDiv.empty();
                paramDiv.append(label);
                paramDiv.append(textArea);

                return AJS.MacroBrowser.Field(paramDiv, input, options);
            }
        }
    }

    //a function to run before an existing macro is loaded into the parameter form fields
    MermaidMacro.prototype.beforeParamsSet = function(selectedParams, macroSelected){
        originalCode = selectedParams.code;
        $("#macro-param-Code").val(originalCode);
        console.log("Orignal Code : " + originalCode);
        return selectedParams;
    };


    //a function to run before the form fields are converted into a macro parameter string
    MermaidMacro.prototype.beforeParamsRetrieved = function(params){
        params.code = $("#macro-param-Code").val();
        console.log("params.code : " + params.code);
        return params;
    };

    //called with the preview iframe element and macro metadata when the user previews the macro
    // MermaidMacro.prototype.postPreview = function(iframe, macro){
    //     console.log("postPreview iframe : " + iframe);

    //     console.log($(AJS.$('.mermaid')));

    //     console.log(typeof mermaid.init(undefined, $(AJS.$('.mermaid'))));
    //     iframe.srcdoc = "hello";

    //     return iframe;
    // }

    AJS.toInit(function(){
        AJS.bind("init.rte", function(){
            AJS.MacroBrowser.setMacroJsOverride('mermaid-macro', new MermaidMacro());
        })
    })
})(AJS.$);

