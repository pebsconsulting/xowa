/*
XOWA: the XOWA Offline Wiki Application
Copyright (C) 2012-2017 gnosygnu@gmail.com

XOWA is licensed under the terms of the General Public License (GPL) Version 3,
or alternatively under the terms of the Apache License Version 2.0.

You may use XOWA according to either of these licenses as is most appropriate
for your project on a case-by-case basis.

The terms of each license can be found in the source code repository:

GPLv3 License: https://github.com/gnosygnu/xowa/blob/master/LICENSE-GPLv3.txt
Apache License: https://github.com/gnosygnu/xowa/blob/master/LICENSE-APACHE2.txt
*/
package gplx.xowa.xtns.rss; import gplx.*; import gplx.xowa.*; import gplx.xowa.xtns.*;
import org.junit.*;
public class Rss_xnde_tst {
	private final Xop_fxt fxt = new Xop_fxt();
	@Before public void init() {fxt.Reset();}
	@Test  public void Basic() {
		fxt.Test_parse_page_all_str("<rss max='6'>http://blog.wikimedia.org/feed/</rss>", "XOWA does not support this extension: &lt;rss max='6'&gt;<a href=\"http://blog.wikimedia.org/feed/\" rel=\"nofollow\" class=\"external free\">http://blog.wikimedia.org/feed/</a>&lt;/rss&gt;");
		fxt.Test_parse_page_all_str("<rss />", "XOWA does not support this extension: &lt;rss /&gt;");
	}
}
