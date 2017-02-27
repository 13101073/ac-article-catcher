package molaga.webmagic;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.PhantomJSDownloader;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

public class Sample implements PageProcessor {
	// set log mode
	private final static boolean useLogger = true;

	//logger
	final static Logger logger = LoggerFactory.getLogger(Sample.class);
	//proxy settings
	final HttpHost proxy = new HttpHost("10.167.251.83", 8080);
	//user agent
	final String UA_EDGE = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; .NET CLR 3.0.30729; .NET CLR 3.5.30729; Tablet PC 2.0; rv:11.0) like Gecko";
	//
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000).setHttpProxy(proxy).setUserAgent(UA_EDGE);

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
//		page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/[\\w\\-]+/[\\w\\-]+)").all());
//        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/[\\w\\-])").all());
//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
//        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
//        if (page.getResultItems().get("name")==null){
//            //skip this page
//            page.setSkip(true);
//        }
//        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
//		TODO nothing.
		//logger.info(page.getHtml().toString());
		//System.out.println(page.getHtml().toString());

		// acfun process.
		processAcfun(page);

	}

	/**
	 * for acfun only
	 * */
	protected void processAcfun(Page p){
		String url = p.getUrl().toString();
		Html h = p.getHtml();
		String lastWord = url.substring(url.lastIndexOf('/'));

		printOrLog("current url: {}", url); /* current url */

		java.util.List<String> next = h.xpath("//a[@class='title']/@href").all();
		printOrLog("next article link size: {}", next.size()); /* link size */

		if(next==null || next.size()==0){
			printOrLog("文章标题： {}", h.xpath("//title/text()")); /* title */

			List<String> a = h.xpath("//div[@id='area-player']/p/text()").all();
			printOrLog("文章内容： "); /* content */
			for(String aa : a)
				printOrLog(aa.trim());

			printOrLog("UP主： {}", h.xpath("//div[@class='u-info']//a//nobr/text()")); /* up */
		} else {
			//p.addTargetRequest(next.get(0));
			p.addTargetRequests(next);
			if(Pattern.compile("(/index)(_\\d)?.htm").matcher(lastWord).matches()){
				String[] arr = lastWord.split("[._]");
				if(arr.length == 2){
					url = url.replace(lastWord, arr[0]+"_2"+'.'+arr[1]);
				}else{
					url = url.replace(lastWord, arr[0]+'_'+(Integer.parseInt(arr[1])+1)+'.'+arr[2]);
				}
				p.addTargetRequest(url);
			}
		}


	}

	@org.junit.Test
	public void tst(){
		String s = "10";
		System.out.println(s.charAt(s.length()-1)+1-48);
	}

	public static void main(String[] args) {
		//Spider.create(new Sample()).addUrl("https://github.com/code4craft").thread(5).run();

		// weibo
		//normal();

		// weka
		//java.util.Scanner s = new java.util.Scanner(System.in);
		//weka(s.nextLine());

		// acfun
		acfun();
	}

	private static void printOrLog(String msg, Object... obj){
		if(useLogger){
			logger.info(msg, obj);
		} else {
			/**use console print ignoring parameter [obj].*/
			System.out.println(msg);
		}

	}


	/**
	 * normal
	 * @usage for s.weibo.com
	 * */
	protected static void normal(){
		String keywd = "";
		try {
			keywd = URLEncoder.encode("吃饱饱睡好好起早早长高高", "utf8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Spider.create(new Sample()).setDownloader(new PhantomJSDownloader()).addPipeline(new FilePipeline()).addUrl("http://s.weibo.com/weibo/"+keywd+"&Refer=index").thread(5).run();
	}


	/**
	 * @usage download weka documetation.
	 * */
	protected static void weka(String resource){
		String resource_not_404;
		if(resource==null){
			resource_not_404 = "CLASSPATH";
		}
		resource_not_404 = resource;
		Spider.create(new Sample()).addUrl("http://weka.wikispaces.com/"+resource_not_404).thread(1).run();
	}

	/**
	 * @usage download acfun essay.
	 * from <link>http://www.acfun.cn/v/list73/index.htm</link>
	 * */
	protected static void acfun(){
		Spider.create(new Sample()).addUrl("http://www.acfun.cn/v/list73/index.htm").thread(1).run();
	}
}
