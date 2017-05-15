package cn.ljj;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HttpUploder {
	private String cookie = "";
	private String uploadBaseUrl = "";
	public HttpUploder(String uploadBaseUrl, String cookie){
		this.uploadBaseUrl = uploadBaseUrl;
		this.cookie = cookie;
	}

	public String uploadFileSync(String path, String contentType) {
		System.out.println("uploadFileSync start path=" + path);
		String url = null;
		String uploadUrl = uploadBaseUrl + cookie;
		try {
			URI uri = new URI(uploadUrl);
			HttpPost post = new HttpPost(uri);
			File file = new File(path);
			MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
			String fileName = "";
			int index = path.lastIndexOf(File.separator);
			if(index > 0){
				fileName = path.substring(index + 1);
				String extention = "";
				int pointIndex = fileName.indexOf('.');
				if(pointIndex > 0){
					extention = fileName.substring(pointIndex);
					fileName = fileName.substring(0, pointIndex);
				}
				fileName = Hex.encodeHexString(fileName.getBytes()) + extention;
			}else{
				fileName = "unNamed";
			}
			mEntityBuilder.addBinaryBody("file", file, ContentType.create(contentType), fileName);
			post.setEntity(mEntityBuilder.build());
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpResponse response = httpclient.execute(post);
			InputStream input = response.getEntity().getContent();
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(new InputStreamReader(input));
			JsonObject object = element.getAsJsonObject();
			url = object.get("url").getAsString();
		} catch (Exception e) {
			System.out.println("uploadFileSync end url=" + url);
			e.printStackTrace();
		}
		System.out.println("uploadFileSync end url=" + url);
		return url;
	}
}