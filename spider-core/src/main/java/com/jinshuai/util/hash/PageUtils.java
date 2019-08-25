package com.jinshuai.util.hash;

import lombok.extern.slf4j.Slf4j;
import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: JS
 * @date: 2019/5/25
 * @description: SimHash进行doc相似度检测
 */
@Slf4j
public class PageUtils {

    private volatile static PageUtils instance;

    private Map<String, Set<BigInteger>> invertedIndex = new ConcurrentHashMap<>();

    /**
     * 测试：跟踪文本
     * */
    private Map<BigInteger, String> fingerContent = new ConcurrentHashMap<>();

    public static PageUtils getInstance() {
        if (instance == null) {
            synchronized (PageUtils.class) {
                if (instance == null) {
                    instance = new PageUtils();
                }
            }
        }
        return instance;
    }

    private PageUtils(){}

    private static final int BIT_SIZE = 64;

    private static final int TABLE_SIZE = 16;

    private static final int HAMMING_DISTANCE = 3;

    private ThreadLocal<String> simhashStrContainer = new ThreadLocal<>();

    public boolean exist(String title, String content) {
        boolean exist = false;
        BigInteger fingerprint = getSimHash(title, content);
//        fingerContent.put(fingerprint, title + "====" + content);
        String hashStr = simhashStrContainer.get();
        // 防止分词错误NPE
        if (hashStr.length() == BIT_SIZE) {
            // 获取每一个table对应的所有候选结果
            for (int start = 0; start < BIT_SIZE; start += TABLE_SIZE) {
                String table = hashStr.substring(start, start + TABLE_SIZE);
                Set<BigInteger> fingerprints = invertedIndex.get(table);
                if (fingerprints != null && fingerprints.size() > 0) {
                    for (BigInteger fingerprintRes : fingerprints) {
                        // 海明距离
                        int hammingDistance = fingerprintRes.xor(fingerprint).bitCount();
                        if (hammingDistance <= HAMMING_DISTANCE) {
//                            log.error("标题 [{}] \r\n 内容[{}] \r\n 与 标题内容[{}] 相似\r\n 汉明距离:[{}]", title, content, fingerContent.get(fingerprintRes), hammingDistance);
                            exist = true;
                            break;
                        }
                    }
                }
                if (exist)
                    break;
            }
            // 构建倒排索引(16 * 4)
            constructInvertedIndex(fingerprint);
        }
        return exist;
    }

    private BigInteger getSimHash(String title, String content) {
        double[] featureVector = new double[BIT_SIZE];
        // 1. 分词,计算权重
        Collection<Keyword> result = getParticiple(title, content);
        // 2. hash
        // 3. 加权
        // 4. 合并
        featureVector = weightingAndCombine(featureVector, result);
        // 5. 降维
        // 6. SimHash 指纹
        return decreaseDimensionAndGetFingerprint(featureVector);
    }

    /**
     * 分词
     * */
    private Collection<Keyword> getParticiple(String title, String content) {
        int keyNumber = content.length() / 2 < 5 ? 5 : content.length(); // TODO
        KeyWordComputer kwc = new KeyWordComputer(keyNumber);
        return kwc.computeArticleTfidf(title, content);
    }

    /**
     * 哈希 加权 合并
     * */
    private double[] weightingAndCombine(double[] featureVector, Collection<Keyword> result) {
        for (Keyword keyword : result) {
            String keyStr = keyword.getName();
            BigInteger keyHash = MurmurHash.hash64(keyStr);
            for (int i = 0; i < BIT_SIZE; i++) {
                final BigInteger bitMask = BigInteger.ONE.shiftLeft(BIT_SIZE - 1 - i);
                // 3. 加权
                // 4. 合并
                if (keyHash.and(bitMask).signum() != 0) {
                    featureVector[i] += keyword.getScore();
                } else {
                    featureVector[i] -= keyword.getScore();
                }
            }
        }
        return featureVector;
    }

    /**
     * 降维 获取指纹
     * */
    private BigInteger decreaseDimensionAndGetFingerprint(double[] featureVector) {
        BigInteger fingerprint = BigInteger.ZERO;
        StringBuilder simHashBuilder = new StringBuilder();
        for (int i = 0; i < BIT_SIZE; i++) {
            BigInteger bitMask = BigInteger.ONE.shiftLeft(BIT_SIZE - 1 - i);
            if (featureVector[i] > 0) {
                fingerprint = fingerprint.or(fingerprint.xor(bitMask));
                simHashBuilder.append(1);
            } else {
                simHashBuilder.append(0);
            }
        }
        simhashStrContainer.set(simHashBuilder.toString());
        return fingerprint;
    }

    /**
     * 构建倒排索引
     * < table, {simhash1, simhash2 simhash3...} >
     *
     * */
    private void constructInvertedIndex(BigInteger fingerprint) {
        String hashStr = simhashStrContainer.get();
        for (int start = 0; start < BIT_SIZE; start += TABLE_SIZE) {
            String table = hashStr.substring(start, start + TABLE_SIZE);
            Set<BigInteger> docs = invertedIndex.get(table);
            if (docs == null) {
                docs = new HashSet<>();
            }
            docs.add(fingerprint);
            invertedIndex.put(table, docs);
        }
    }

    public static void main(String[] args) throws IOException {
        PageUtils pageUtil = PageUtils.getInstance();
        StringBuilder sb1 = new StringBuilder();
        File file1 = new File("D:/Data/1.txt");
        Files.readAllLines(file1.toPath()).forEach(line ->{
            sb1.append(line);
        });

        StringBuilder sb2 = new StringBuilder();
        File file2 = new File("D:/Data/2.txt");
        Files.readAllLines(file2.toPath()).forEach(line -> {
            sb2.append(line);
        });

        pageUtil.exist("学校党委理论学习中心组召开扩大会议",sb1.toString());
        pageUtil.exist("校党委理论学习中心组召开专题会议学习传达全国“两会”精神", sb2.toString());


//        BigInteger simhash1 = pageUtil.getSimHash("","我来自河北省，你们可以叫我金帅");
//        BigInteger simhash2 = pageUtil.getSimHash("","我来自河北省，我是金帅");
//        System.out.println(simhash1.xor(simhash2).bitCount());
//        System.out.println(simhash1.xor(simhash2).toString(2));
//        System.out.println("=========十进制========");
//        System.out.println(simhash1.toString());
//        System.out.println(simhash2.toString());
//        System.out.println("=========二进制========");
//        System.out.println(simhash1.toString(2));
//        System.out.println(simhash2.toString(2));

    }

}
