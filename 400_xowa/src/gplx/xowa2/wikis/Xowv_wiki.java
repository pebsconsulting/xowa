/*
XOWA: the XOWA Offline Wiki Application
Copyright (C) 2012 gnosygnu@gmail.com

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package gplx.xowa2.wikis; import gplx.*; import gplx.xowa2.*;
import gplx.core.primitives.*;
import gplx.xowa.*; import gplx.xowa.wikis.xwikis.*; import gplx.xowa.langs.cases.*; import gplx.xowa.wikis.ttls.*; import gplx.xowa.html.hzips.*;
import gplx.xowa.wikis.data.*;
import gplx.xowa.files.*; import gplx.xowa.files.origs.*; import gplx.xowa.files.fsdb.*; import gplx.xowa.files.bins.*;
import gplx.xowa.wikis.data.tbls.*; import gplx.dbs.*; import gplx.xowa.html.hdumps.*; import gplx.xowa.wikis.*; import gplx.xowa.files.repos.*;
import gplx.xowa2.apps.*; import gplx.xowa2.wikis.specials.*; import gplx.xowa2.gui.*;
import gplx.fsdb.*; import gplx.fsdb.meta.*;
public class Xowv_wiki implements Xow_wiki, Xow_ttl_parser {
	private final Xof_fsdb_mgr__sql fsdb_mgr; private Fsdb_db_mgr db_core_mgr;
	public Xowv_wiki(Xoav_app app, byte[] domain_bry, Io_url wiki_root_dir) {
		this.app = app;
		this.domain_bry = domain_bry; this.domain_str = String_.new_utf8_(domain_bry); 
		this.domain_itm = Xow_domain_.parse(domain_bry);
		this.domain_tid = domain_itm.Domain_tid();
		this.domain_abrv = Xow_wiki_alias.Build_alias(Xow_domain_.parse(domain_bry));
		this.ns_mgr = Xow_ns_mgr_.default_(app.Utl_case_mgr());
		this.html_mgr__hzip_mgr = new Xow_hzip_mgr(app.Usr_dlg(), this);
		this.html_mgr__hdump_rdr = new Xohd_hdump_rdr(app, this);
		this.xwiki_mgr = new Xow_xwiki_mgr();
		this.special_mgr = new Xosp_special_mgr(this);
		Io_url wiki_file_dir = domain_tid == Xow_domain_.Tid_int_home ? wiki_root_dir : wiki_root_dir.OwnerDir().OwnerDir().GenSubDir_nest("file", domain_str);
		this.fsys_mgr = new Xow_fsys_mgr(wiki_root_dir, wiki_file_dir);
		this.fsdb_mgr = new Xof_fsdb_mgr__sql();
	}
	public Xoa_app				App() {return app;}
	public byte[]				Domain_bry() {return domain_bry;} private final byte[] domain_bry;
	public String				Domain_str() {return domain_str;} private final String domain_str;
	public Xow_domain			Domain_itm() {return domain_itm;} private final Xow_domain domain_itm;
	public int					Domain_tid() {return domain_tid;} private final int domain_tid;
	public byte[]				Domain_abrv() {return domain_abrv;} private final byte[] domain_abrv;
	public Xow_ns_mgr			Ns_mgr() {return ns_mgr;} private final Xow_ns_mgr ns_mgr;
	public Xow_fsys_mgr			Fsys_mgr() {return fsys_mgr;} private Xow_fsys_mgr fsys_mgr;
	public Xowd_db_mgr			Data_mgr__core_mgr() {return data_mgr__core_mgr;} private Xowd_db_mgr data_mgr__core_mgr;
	public Xow_repo_mgr			File_mgr__repo_mgr() {return file_mgr__repo_mgr;} private Xowv_repo_mgr file_mgr__repo_mgr = new Xowv_repo_mgr();
	public Xof_fsdb_mode		File_mgr__fsdb_mode() {return file_mgr__fsdb_mode;} private final Xof_fsdb_mode file_mgr__fsdb_mode = Xof_fsdb_mode.new_view();
	public Xof_orig_mgr			File_mgr__orig_mgr() {return orig_mgr;} private final Xof_orig_mgr orig_mgr = new Xof_orig_mgr();
	public Xof_bin_mgr			File_mgr__bin_mgr() {return fsdb_mgr.Bin_mgr();}
	public Fsm_mnt_mgr			File_mgr__mnt_mgr() {return fsdb_mgr.Mnt_mgr();}
	public boolean					Html_mgr__hdump_enabled() {return Bool_.Y;}
	public Xow_hzip_mgr			Html_mgr__hzip_mgr() {return html_mgr__hzip_mgr;} private final Xow_hzip_mgr html_mgr__hzip_mgr;
	public Xohd_hdump_rdr		Html_mgr__hdump_rdr() {return html_mgr__hdump_rdr;} private final Xohd_hdump_rdr html_mgr__hdump_rdr;
	public Xol_lang				Lang() {throw Err_.not_implemented_();}

	public Xosp_special_mgr Special_mgr() {return special_mgr;} private Xosp_special_mgr special_mgr;
	public Xow_xwiki_mgr Xwiki_mgr() {return xwiki_mgr;} private Xow_xwiki_mgr xwiki_mgr;
	public Xoav_app Appv() {return app;} private final Xoav_app app;
	private boolean init_needed = true;
	public void Pages_get(Xog_page rv, Gfo_url url, Xoa_ttl ttl) {
		if (init_needed) {
			init_needed = false;
			if (!String_.Eq(domain_str, "xowa")) {;				// FIXME: ignore "xowa" for now; WHEN:converting xowa to sqlitedb
				data_mgr__core_mgr = new Xowd_db_mgr(fsys_mgr.Root_dir(), domain_itm);
				Io_url core_url = gplx.xowa.wikis.Xow_fsys_mgr.Find_core_fil(fsys_mgr.Root_dir(), domain_str);
				data_mgr__core_mgr.Init_by_load(core_url);
				this.db_core_mgr = Fsdb_db_mgr_.new_detect(domain_str, fsys_mgr.Root_dir(), fsys_mgr.File_dir());
				if (db_core_mgr != null)	// will be null for xowa db
					fsdb_mgr.Mnt_mgr().Ctor_by_load(db_core_mgr);
				file_mgr__repo_mgr.Add_repo(app, fsys_mgr.File_dir(), Bry_.new_utf8_("commons.wikimedia.org"), Bry_.new_utf8_("simple.wikipedia.org"));
				file_mgr__repo_mgr.Add_repo(app, fsys_mgr.File_dir(), Bry_.new_utf8_("simple.wikipedia.org"), Bry_.new_utf8_("commons.wikimedia.org"));
				orig_mgr.Init_by_wiki(file_mgr__fsdb_mode, db_core_mgr.File__orig_tbl_ary(), domain_bry, app.Wmf_mgr().Download_wkr(), file_mgr__repo_mgr, Xof_url_bldr.new_v2_());
				fsdb_mgr.Init_by_wiki(this);
				data_mgr__core_mgr.Db__core().Tbl__ns().Select_all(ns_mgr);
				html_mgr__hdump_rdr.Init_by_db(data_mgr__core_mgr);
			}
		}
		if (ttl.Ns().Id_special())
			special_mgr.Get_by_ttl(rv, url, ttl);
		else
			html_mgr__hdump_rdr.Get_by_ttl(rv, ttl);
	}
	public Xoa_ttl Ttl_parse(byte[] ttl) {return Xoa_ttl.parse(app.Utl__bfr_mkr(), app.Utl_amp_mgr(), app.Utl_case_mgr(), xwiki_mgr, ns_mgr, app.Utl_msg_log(), ttl, 0, ttl.length);}
	public Xoa_ttl Ttl_parse(int ns_id, byte[] ttl) {
		Xow_ns ns = ns_mgr.Ids_get_or_null(ns_id);
		byte[] raw = Bry_.Add(ns.Name_db_w_colon(), ttl);
		return Xoa_ttl.parse(app.Utl__bfr_mkr(), app.Utl_amp_mgr(), app.Utl_case_mgr(), xwiki_mgr, ns_mgr, app.Utl_msg_log(), raw, 0, raw.length);
	}
}