///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.knight.engine.impl.charfilter;
//
//import com.knight.engine.impl.charfilter.CharFilterImpl;
//import com.knight.util.Utils;
////import com.yz.gamexp.util.dirtyword.DirtyWordsManager;
//import com.yz.gamexp.util.dirtyword.DirtyWordsManager;
//import java.util.Map;
//import org.dom4j.DocumentException;
//
///**
// *
// * @author Rjx
// */
//public class TestCharFilter {
//
//    private static String[] testArray = {
//        "共abcbc产**  __----*党你好",
//        "我日你",
//        "fuck",
//        "FUCK",
//        "Fuck",
//        "Fuck fuck,fUCK",
//        "f u c k you for ever,性爱I am a good boy,顶你个肺性爱喂",
//        "从前有个小朋友他很乖asdf很听话adsfa，你话怎么就怎么啦，喂，我纯粹来测试下的，不知道有无问题，其实叫你做下，点解，无啊，废话", //        "tell me why",
//        "出售货真价实的XXXX，要的代价",
//        "你 好 吗，性 爱 我 最 喜 欢",
//        "Hi,I make a mistake",
//        "这游戏真烂",
//        "好卡啊，破游戏",
//        "这件装备几多钱哈",
//        "自己带价",
//        "5个锻造宝石+20个幸运",
//        "军团、武将、背包、排行榜、娱乐坊、食色性也",
//        "大家赶紧做任务哈",
//        "50副本门口等，3缺1",
//        "有人组队吗",
//        "杀怪任务有人来吗,昆仑之巅地图",
//        "有人能帮忙杀个怪吗",
//        "我日你，你就一二逼",
//        "他这个坑爹货，真一傻B，法轮功是屏蔽字，XXX是屏蔽字，89，,64,8 9,6 4",
//        "官方是盈正，为什么有客服代表和系统管理员，GM回家吧，问候你全家，色情网站我最爱，波霸至中意，有无推荐咩成人网站同成人小说啊，成人漫画都得，唔该晒",
//        "性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性性",
//        "雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮雪山狮"
//    };
//
//    public static void main(String[] args) throws DocumentException, Exception {
//        CharFilterImpl manager = new CharFilterImpl();
//        manager.init(Utils.getXmlRootElement("D:\\KeyWords.xml"));
//
//        System.out.println("测试过滤开始===================");
//        int all = 0;
//        for (String s : CharFilterImpl.set) {
//            String r = manager.replaceDiryWords(s, "[屏蔽]", true, true);
//            if (!r.equals("[屏蔽]")) {
//                System.out.println(s + "," + r);
//                all++;
//            }
//        }
//        System.out.println("总不符合：" + all);
//
//        System.out.println("测试过滤结束===================");
////        System.out.println(manager.replaceDiryWords("F u C k", "[测试屏蔽]", true));
////        System.out.println(manager.replaceDiryWords("F u C k", "[测试屏蔽]", true, true));
//
//        System.out.println((int) Character.MIN_HIGH_SURROGATE + "," + (int) Character.MAX_HIGH_SURROGATE);
//
//        for (String test : testArray) {
//            System.out.println(manager.replaceDiryWords(test, "[屏蔽]", true, true));
//        }
//        char[] c = new char[]{(char) 31};
//        System.out.println(manager.checkWords(new String(c), true, true, true, true));
//        DirtyWordsManager r = DirtyWordsManager.instance();
//        r.addWordsWithXmlUrl("D:\\KeyWords.xml");
//        r.replace("XXXX", "[屏蔽]", true,true);
//        int total = 50000;
//        String replaced = "[屏蔽]";
//
//        for (Map.Entry entry : CharFilterImpl.getStatistics().entrySet()) {
//            System.out.println(entry);
//        }
//
//        System.out.println("各位观众，正式开始");
//        String[] testQueue = testArray;
//        int size = testQueue.length;
//        for (int j = 0; j < 10; j++) {
//            long start = System.nanoTime();
//            for (int i = 0; i < total; i++) {
//                for (int z = size; --z >= 0;) {
//                    r.replace(testQueue[z], replaced, true, true);
//                }
//            }
//            System.out.println("====海爷消耗时间：" + (System.nanoTime() - start) / 1000000);
//            start = System.nanoTime();
//            for (int i = 0; i < total; i++) {
//                for (int z = size; --z >= 0;) {
//                    manager.replaceDiryWords(testQueue[z], replaced, true, true);
//                }
//            }
//            System.out.println("====knight消耗时间：" + (System.nanoTime() - start) / 1000000);
//        }
//    }
//}
