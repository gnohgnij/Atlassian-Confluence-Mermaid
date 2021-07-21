package org.anvard.atlassian;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.RequiredResources;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;

public class MermaidMacroTest {

	private MermaidMacro macro;
	private PageContext pageContext;
	private ConversionContext context;
	
	@Before
	public void setUp() {
		pageContext = mock(PageContext.class);
		context = mock(ConversionContext.class);
		when(pageContext.getOutputType()).thenReturn("display");
		when(context.getPageContext()).thenReturn(pageContext);
		
		RequiredResources resources = mock(RequiredResources.class);
		WebResourceAssembler assembler = mock(WebResourceAssembler.class);
		PageBuilderService pageBuilder = mock(PageBuilderService.class);
		when(assembler.resources()).thenReturn(resources);
		when(pageBuilder.assembler()).thenReturn(assembler);
		macro = new MermaidMacro(pageBuilder, null, null);
	}
	
	@Test
	public void xssPrevention() throws Exception {
		String bad = "<script>window.alert('abcde')</script>";
		String output = macro.execute(null, bad, context);
		assertEquals("XSS vulnerability", output.lastIndexOf("<script>"), -1);
		assertEquals("XSS vulnerability", output.lastIndexOf("</script>"), -1);
	}
}
