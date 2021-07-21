AJS.toInit(function() {
    mermaid.initialize({
        startOnLoad:false
    });

    AJS.$(".mermaid").each(function(i, e) {
        console.log(e);
        console.log($(e));
        var timer = setInterval(function() {
            if ($(e).is(":visible")) {
                mermaid.init(undefined, $(e));
                clearInterval(timer);
            }
        }, 300);
    });
})