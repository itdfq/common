package com.itdfq.common.aop.moreIn;

import java.util.List;

/**
 * @author dfq 2025/3/12 15:01
 * @implNote
 */
public class MergeFunction implements ResultProcessor {
    @Override
    public Object handle(List t) {
        if (t==null){
            return null;
        }
        if(t.size()<=1){
            return t.get(0);
        }
        List first = (List) t.get(0);
        for (int i = 1; i < t.size(); i++){
            List second = (List) t.get(i);
            first.addAll(second);
        }
        return first;
    }
}
