package com.theprogrammingturkey;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

public class BrowserTab
{
	private TextField urlInput;
	private HBox document;
	private TextArea tempText;

	public BrowserTab()
	{

	}

	public Tab initTab()
	{
		urlInput = new TextField();
		urlInput.setPromptText("URL");
		HBox.setHgrow(urlInput, Priority.ALWAYS);
		Button goBtn = new Button("GO");
		goBtn.setOnAction(e -> loadURL(urlInput.getText()));
		HBox urlBar = new HBox(urlInput, goBtn);
		tempText = new TextArea();
		HBox.setHgrow(tempText, Priority.ALWAYS);
		document = new HBox(tempText);
		VBox.setVgrow(document, Priority.ALWAYS);
		VBox content = new VBox(urlBar, document);
		return new Tab("Tab", content);
	}

	public void loadURL(String url)
	{
		if(!url.matches(".+://.+"))
			url = "http://" + url;

		try
		{
			Document page = Jsoup.parse(new URL(url), 5000);
			page.outputSettings().prettyPrint(true);
			tempText.setWrapText(true);
			tempText.setText(page.html());
		} catch(IOException e)
		{
			StringBuilder error = new StringBuilder();
			error.append(e).append("\n");
			StackTraceElement[] trace = e.getStackTrace();
			for(StackTraceElement traceElement : trace)
				error.append("\tat").append(traceElement).append("\n");

			Throwable cause = e.getCause();
			if(cause != null)
			{
				error.append("Caused by: ").append(cause).append("\n");
				for(StackTraceElement traceElement : cause.getStackTrace())
					error.append("\tat").append(traceElement).append("\n");
			}

			tempText.setText(error.toString());
		}
	}
}
