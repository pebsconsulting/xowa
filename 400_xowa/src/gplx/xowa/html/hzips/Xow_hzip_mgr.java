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
package gplx.xowa.html.hzips; import gplx.*; import gplx.xowa.*; import gplx.xowa.html.*;
import gplx.core.primitives.*; import gplx.core.btries.*; import gplx.xowa.wikis.ttls.*;
public class Xow_hzip_mgr {		
	private final Gfo_usr_dlg usr_dlg;
	private byte[] page_url; private byte[] src; private int src_len;
	public Xow_hzip_mgr(Gfo_usr_dlg usr_dlg, Xow_ttl_parser ttl_parser) {
		this.usr_dlg = usr_dlg;
		itm__anchor = new Xow_hzip_itm__anchor(this, ttl_parser);
		itm__header = new Xow_hzip_itm__header(this);
	}
	public Xow_hzip_itm__anchor Itm__anchor() {return itm__anchor;} private Xow_hzip_itm__anchor itm__anchor;
	public Xow_hzip_itm__header Itm__header() {return itm__header;} private Xow_hzip_itm__header itm__header;
	public void Write(Bry_bfr bfr, Xodump_stats_itm stats, byte[] page_url, byte[] src) {
		this.page_url = page_url; this.src = src; this.src_len = src.length;			
		bfr.Clear(); stats.Clear();
		int pos = 0, add_bgn = -1;
		while (pos < src_len) {
			byte b = src[pos];
			Object o = btrie.Match_bgn_w_byte(b, src, pos, src_len);
			if (o == null) {
				if (add_bgn == -1) add_bgn = pos;
				++pos;
			}
			else {
				if (add_bgn != -1) {bfr.Add_mid(src, add_bgn, pos); add_bgn = -1;}
				byte tid = ((Byte_obj_val)o).Val();
				int match_bgn = pos;
				int match_end = btrie.Match_pos();
				switch (tid) {
					case Tid_a_lhs: pos = itm__anchor.Save(bfr, stats, src, src_len, match_bgn, match_end); break;
					case Tid_a_rhs: pos = itm__anchor.Save_a_rhs(bfr, stats, src, src_len, match_bgn, match_end); break;
					case Tid_h_lhs: pos = itm__header.Save(bfr, stats, src, src_len, match_bgn, match_end); break;
					default:		Warn_by_pos("save.tid.unknown", match_bgn, match_end); pos = match_end; continue;
				}
				if (pos == Unhandled) {
					bfr.Add_mid(src, match_bgn, match_end);
					pos = match_end;
				}
			}
		}
		if (add_bgn != -1) bfr.Add_mid(src, add_bgn, src_len);
	}
	public byte[] Parse(Bry_bfr rv, byte[] page_url, byte[] src, Ordered_hash redlink_uids) {
		this.page_url = page_url; this.src = src;
		this.src_len = src.length;			
		int pos = 0, add_bgn = -1;
		rv.Clear();
		while (pos < src_len) {
			byte b = src[pos];
			if (b == Xow_hzip_dict.Escape) {
				if (add_bgn != -1) {rv.Add_mid(src, add_bgn, pos); add_bgn = -1;}
				int itm_pos = pos + 2;		
				int tid_pos = pos + 1;							if (tid_pos >= src_len) {Warn_by_pos("load.eos", pos, itm_pos); break;}
				byte tid = src[tid_pos];
				switch (tid) {
					case Xow_hzip_dict.Tid_lnki_text_n:
					case Xow_hzip_dict.Tid_lnki_text_y:			pos = itm__anchor.Load_lnki(rv, src, src_len, itm_pos, tid, redlink_uids); break;
					case Xow_hzip_dict.Tid_lnke_txt:
					case Xow_hzip_dict.Tid_lnke_brk_text_n:
					case Xow_hzip_dict.Tid_lnke_brk_text_y:		pos = itm__anchor.Load_lnke(rv, src, src_len, itm_pos, tid); break;
					case Xow_hzip_dict.Tid_a_rhs:				pos = itm_pos; rv.Add_str("</a>"); break;
					case Xow_hzip_dict.Tid_hdr_lhs:				pos = itm__header.Load(rv, src, src_len, itm_pos); break;
					default:									pos = itm_pos; Warn_by_pos("hzip.load.unknown", pos, itm_pos); break;	// NOTE: should not happen, but handle else infinite loop; DATE:2015-06-08
				}
			}
			else {
				if (add_bgn == -1) add_bgn = pos;
				++pos;
			}
		}
		if (add_bgn != -1) rv.Add_mid(src, add_bgn, src_len);
		return rv.Xto_bry_and_clear();
	}
	public int Warn_by_pos_add_dflt(String err, int bgn, int end)	{return Warn_by_pos(err, bgn, end, 32);}
	public int Warn_by_pos(String err, int bgn, int end)			{return Warn_by_pos(err, bgn, end, 0);}
	private int Warn_by_pos(String err, int bgn, int end, int end_adj) {
		end += end_adj; if (end > src_len) end = src_len;
		usr_dlg.Warn_many("", "", "hzip failed: page=~{0} err=~{1} mid=~{2}", String_.new_u8(page_url), err, String_.new_u8(src, bgn, end));
		return end + 1;
	}
	public static final int Unhandled = -1;
	private static final byte 
	  Tid_a_lhs = 0
	, Tid_a_rhs = 1
	, Tid_h_lhs = 2
	;
	private Btrie_slim_mgr btrie = Btrie_slim_mgr.cs()
	.Add_str_byte("<a "			, Tid_a_lhs)
	.Add_str_byte("</a>"		, Tid_a_rhs)
//		.Add_str_byte("<h"			, Tid_h_lhs)
	;
}
