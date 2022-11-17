package com.mobilesalesperson.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

import com.mobilesalesperson.database.Encryptor;
import com.mobilesalesperson.database.MspDBHelper;

public class FileLoader {

	private Context context;
	private String currentTable;
	private MspDBHelper dbHelpher;
	private Encryptor encryptor;
	private String salespersonFolder;
	private List<String> availFiles;

	public FileLoader(Context context,MspDBHelper dbHelpher,Encryptor encryptor,String salespersonFolder) {
		this.context = context;
		this.dbHelpher = dbHelpher;
		this.encryptor = encryptor;
		this.salespersonFolder = salespersonFolder;
		availFiles = new ArrayList<String>();
	}

	public List<List<String>> parseDocument(InputStream inputStream) {

		List<List<String>> fResultLst = new ArrayList<List<String>>();
		
		List<String> rsltLst = new ArrayList<String>();
		
		String result = "";
		

		SAXParserFactory spf = SAXParserFactory.newInstance();

		try {
			SAXParser sp = spf.newSAXParser();
			XMLReader xmlReader = sp.getXMLReader();
			XmlHandeler h = new XmlHandeler();
			xmlReader.setContentHandler(h);
			InputSource inputSource = new InputSource();
			inputSource.setEncoding("UTF-8");
			inputSource.setByteStream(inputStream);
			xmlReader.parse(inputSource);

			result = "success";
		} catch (SAXException se) {
			se.printStackTrace();
			result = "Parsing error";
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			result = "Parsing error";
		} catch (IOException ie) {
			ie.printStackTrace();
			result = "Parsing error";
		}
		
		rsltLst.add(result);
		
		fResultLst.add(rsltLst);
		fResultLst.add(availFiles);

		return fResultLst;
	}

	class XmlHandeler extends DefaultHandler {
		
		private StringBuffer tempVal;

		@Override
		public void startDocument() throws SAXException {
		}

		@Override
		public void endDocument() throws SAXException {
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			super.startElement(uri, localName, qName, attributes);
			
			tempVal = new StringBuffer();
			
			System.out.println("Start:"+ localName);
			
			
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			tempVal.append(new String(ch, start, length));
			
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			
			System.out.println("End:"+ localName);
			
			
			
			if(!localName.equals("MSPData")){
				
				availFiles.add(localName);
				
				File common_path = encryptor.createCommonPath();
				File folder_path = new File(common_path, salespersonFolder);
				
				File file = new File(folder_path, localName+".txt");
				
				try {
					
					if(!folder_path.exists()){
						folder_path.mkdirs();
					}
					
					if(!file.exists()){
						file.createNewFile();
					}
					
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(tempVal.toString().getBytes());
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
			
		}

	}

}
