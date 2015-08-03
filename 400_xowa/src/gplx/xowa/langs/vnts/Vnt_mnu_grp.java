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
package gplx.xowa.langs.vnts; import gplx.*; import gplx.xowa.*; import gplx.xowa.langs.*;
public class Vnt_mnu_grp {
	private final Ordered_hash list = Ordered_hash_.new_bry_();
	public int Len() {return list.Count();}
	public boolean Has(byte[] key) {return list.Has(key);}
	public Vnt_mnu_itm Get_at(int i) {return (Vnt_mnu_itm)list.Get_at(i);}
	public void Add(Vnt_mnu_itm itm) {list.Add_if_dupe_use_1st(itm.Key(), itm);}
	public Vnt_mnu_itm Get_by(byte[] key) {return (Vnt_mnu_itm)list.Get_by(key);}
}
