AJS.toInit(function() {
    console.log("init");
    mermaid.initialize({
        startOnLoad:false
    });

    AJS.$(".mermaid").each(function(i, e) {
        var timer = setInterval(function() {
            if ($(e).is(":visible")) {
                console.log("visible");
                mermaid.init(undefined, $(e));
                clearInterval(timer);
            }
        }, 300);
    });
})