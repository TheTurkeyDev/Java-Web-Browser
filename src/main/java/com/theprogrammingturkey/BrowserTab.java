package com.theprogrammingturkey;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;

public class BrowserTab
{
	private Tab thisTab;
	private TextField urlInput;
	private VBox document;
	private ElementWrapper documentElem;

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
		document = new VBox();
		ScrollPane scroll = new ScrollPane(document);
		VBox.setVgrow(scroll, Priority.ALWAYS);
		VBox.setVgrow(document, Priority.ALWAYS);
		VBox content = new VBox(urlBar, scroll);
		thisTab = new Tab("Tab", content);
		return thisTab;
	}

	public void resetDocument()
	{
		document.getChildren().clear();
	}

	public void loadURL(String url)
	{
		resetDocument();
		if(!url.matches(".+://.+"))
			url = "http://" + url;

		try
		{
			Document page = Jsoup.parse(new URL(url), 5000);
			documentElem = new ElementWrapper(page, null);
			genPart(document, documentElem);

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
		}
	}

	public void genPart(Pane parentPane, ElementWrapper parentElement)
	{
		for(Element e : parentElement.getElement().children())
		{
			ElementWrapper elem = new ElementWrapper(e, parentElement);
			if(e.tagName().equals("head"))
			{
				loadPageHead(e);
				continue;
			}

			if(e == parentElement.getElement().root())
				continue;

			if(e.childrenSize() == 0)
			{
				Label label = new Label();
				label.setWrapText(true);
				Font font = CSSManager.computeFont(elem.getCSSParsed());
				label.setFont(font);
				label.setText(e.text());
				VBox subPane = new VBox(label);
				VBox.setMargin(subPane, CSSManager.computeMargin(elem.getCSSParsed()));
				parentPane.getChildren().add(subPane);
			}
			else
			{
				VBox subPane = new VBox();
				VBox.setMargin(subPane, CSSManager.computeMargin(elem.getCSSParsed()));
				parentPane.getChildren().add(subPane);
				genPart(subPane, elem);
			}
		}
	}

	public void loadPageHead(Element head)
	{
		for(Element e : head.children())
		{
			if(e.tagName().equals("title"))
				thisTab.setText(e.text());
		}
	}
}
