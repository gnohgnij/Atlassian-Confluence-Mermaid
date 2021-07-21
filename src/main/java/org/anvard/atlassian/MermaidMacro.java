package org.anvard.atlassian;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.importexport.resource.DownloadResourceWriter;
import com.atlassian.confluence.importexport.resource.ExportDownloadResourceManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.atlassian.webresource.api.assembler.PageBuilderService;

public class MermaidMacro implements Macro {

	private static final Logger log = LoggerFactory.getLogger(MermaidMacro.class);

	private static final String[] UNMODIFIED = { "->>", "<<-", "->", "<-", "<=", "=>" };
	private static final String[] MODIFIED = { "-##", "##-", "-#", "#-", "#=", "=#" };

	private PageBuilderService pageBuilderService;
	private ExportDownloadResourceManager exportDownloadResourceManager;

	public MermaidMacro(PageBuilderService pageBuilderService,
			BootstrapManager bootstrapManager, GateKeeper gateKeeper) {
		this.pageBuilderService = pageBuilderService;
		this.exportDownloadResourceManager = new ExportDownloadResourceManager(bootstrapManager, gateKeeper);
	}

	@Override
	public String execute(Map<String, String> parameters, String body, ConversionContext context)
			throws MacroExecutionException {
		String outputType = context.getPageContext().getOutputType();
		String theme = null;
		if (parameters != null){
			theme = parameters.get("Theme");
			if (theme == null || theme == "") {
				theme = "default";
			}
		}			
		String width = null;
		if (parameters != null){
			width = parameters.get("Width");
			if (width == null || width == "") {
				width = "100%";
			}
		}

		String code = null;
		if (parameters != null){
			code = parameters.get("Code");
		}

		if (ConversionContextOutputType.PDF.value().equals(outputType)
				|| ConversionContextOutputType.WORD.value().equals(outputType)
				|| ConversionContextOutputType.FEED.value().equals(outputType)
				|| ConversionContextOutputType.EMAIL.value().equals(outputType)) {
			return renderImage(code);
		} else {
			pageBuilderService.assembler().resources().requireContext("mermaid-plugin");
			return renderDynamic(code, theme, width);
		}
	}

	@Override
	public BodyType getBodyType() {
		return BodyType.NONE;
	}

	@Override
	public OutputType getOutputType() {
		return OutputType.BLOCK;
	}

	private String renderImage(String body) {
		File tempDirectory = GeneralUtil.getLocalTempDirectory();
		File umlFile = null;
		try {
			umlFile = File.createTempFile("mermaid", ".uml", tempDirectory);
			File logFile = File.createTempFile("mermaid", "log", tempDirectory);
			FileWriter umlWriter = new FileWriter(umlFile);
			umlWriter.write(body);
			umlWriter.close();

			String command = fetchMermaidCommand();
			ProcessBuilder pb = new ProcessBuilder(command, "-o", tempDirectory.getAbsolutePath(),
					umlFile.getAbsolutePath());
			pb.redirectOutput(logFile);
			pb.redirectErrorStream(true);
			Process p = pb.start();
			if (p.waitFor(10, TimeUnit.SECONDS)) {
				String pngFilename = umlFile.getAbsolutePath() + ".png";
				File pngFile = new File(pngFilename);
				String result = copyImageToOutput(pngFile);
				pngFile.delete();
				logFile.delete();
				return result;
			} else {
				log.warn("Converting Mermaid UML to PNG failed to complete within timeout");
			}
		} catch (IOException e) {
			log.warn("Failed to convert Mermaid UML to PNG", e);
		} catch (InterruptedException e) {
			log.warn("Interrupted converting Mermaid UML to PNG", e);
		} finally {
			if (null != umlFile) {
				umlFile.delete();
			}
		}
		return "[!]Mermaid UML";
	}

	private String copyImageToOutput(File imageFile) throws IOException {
		ConfluenceUser user = AuthenticatedUserThreadLocal.get();
		String userName = user == null ? "" : user.getName();
		DownloadResourceWriter writer = exportDownloadResourceManager.getResourceWriter(userName, "mermaid", ".png");
		OutputStream outputStream = writer.getStreamForWriting();

		try {
			IOUtils.copy(new FileInputStream(imageFile), outputStream);
			outputStream.close();
		} catch (IOException e) {
			log.warn("Writing Mermaid image to final output failed", e);
			throw e;
		}
		return "<img src=\"" + writer.getResourcePath() + "\"/>";
	}

	private String renderDynamic(String body, String theme, String width) {
		// log.warn("################################ BODY #################################### " + body);

		String modifiedBody = StringUtils.replaceEach(body, UNMODIFIED, MODIFIED);
		String escapedBody = HtmlEscaper.escapeAll(modifiedBody, false);
		String finalBody = StringUtils.replaceEach(escapedBody, MODIFIED, UNMODIFIED);
		StringBuilder sb = new StringBuilder();
		
		sb.append("<div class=\"mermaid\" style=\"overflow-x: auto; width: "+width+"\">");
		sb.append("\n%%{init: {'theme':'"+theme+"'}}%%\n");
		sb.append(finalBody);
		sb.append("</div>");

		log.warn("################################ FINAL BODY #################################### \n" + finalBody);
		log.warn("################################ SB #################################### \n" + sb.toString());

		return sb.toString();
	}

	private String fetchMermaidCommand() {
		return "mermaid";
	}

}
