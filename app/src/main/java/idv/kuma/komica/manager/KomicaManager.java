package idv.kuma.komica.manager;

import java.util.ArrayList;
import java.util.List;

import idv.kuma.komica.entity.KomicaMenuGroup;
import idv.kuma.komica.entity.KomicaMenuMember;

/**
 * Created by TakumaLee on 2016/12/6.
 */

public class KomicaManager {

    private List<KomicaMenuGroup> menuGroupList;

    public class WebType {
        // new, live
        public static final int NORMAL = 1;
        public static final int WEB = 10;
    }

    private static class SingletonHolder {
        private static KomicaManager INSTANCE = new KomicaManager();
    }

    public static KomicaManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public KomicaManager() {
        this.menuGroupList = new ArrayList<>();
    }

    public void setMenuGroupList(List<KomicaMenuGroup> menuGroupList) {
        this.menuGroupList = menuGroupList;
    }

    public KomicaMenuMember findMemberByMemberId(int memberId) {
        KomicaMenuMember member = null;
        for (KomicaMenuGroup group : menuGroupList) {
            if (memberId >= group.getMemberList().size()) {
                continue;
            } else {
                member = group.getMemberList().get(memberId - group.getMemberList().get(0).getMemberId());
                break;
            }
        }
        return member;
    }

    public List<KomicaMenuGroup> getMenuGroupList() {
        return menuGroupList;
    }

    public int checkWebType(String menuStr) {
        switch (menuStr) {
            case "新番捏他":
            case "新番實況":
                return WebType.NORMAL;
            default:
                return WebType.WEB;
        }
    }
}
