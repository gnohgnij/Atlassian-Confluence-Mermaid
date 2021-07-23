package dso.plugin;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by ahohn on 9/4/16.
 */
public class MermaidScriptTest {

	@Ignore
    @Test
    public void runMermaid() throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByMimeType("application/javascript");
        engine.put("x","hello");
        engine.eval("print(x)");
        engine.eval("var global = this;\n" +
                "var window = this;\n" +
                "var SVGElement = {prototype: {}};\n" +
                "var console = {};\n" +
                "console.debug = print;\n" +
                "console.warn = print;\n" +
                "console.log = print;");
        InputStream mermaid = MermaidScriptTest.class.getResourceAsStream("/js/mermaidAPI.min.js");
        engine.eval(new InputStreamReader(mermaid));
        engine.eval("mermaidAPI.parseError = function(err,hash){\n" +
                "    print(err);\n" +
                "};");
        engine.eval("mermaidAPI.initialize({logLevel: 1})");
        engine.put("graphDefinition", "graph TB\\na-->b");
        engine.eval("var cb = function(html){\n" +
                "        console.log(html);\n" +
                "    }");
        engine.eval("mermaidAPI.render('id1',graphDefinition,cb);");
    }
}
