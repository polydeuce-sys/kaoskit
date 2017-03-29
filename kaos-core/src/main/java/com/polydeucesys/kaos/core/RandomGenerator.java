package com.polydeucesys.kaos.core;
/*
 * Copyright (c) 2016 Polydeuce-Sys Ltd
 *  
 *
 *       Licensed under the Apache License, Version 2.0 (the "License");
 *       you may not use this file except in compliance with the License.
 *       You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *       Unless required by applicable law or agreed to in writing, software
 *       distributed under the License is distributed on an "AS IS" BASIS,
 *       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *       See the License for the specific language governing permissions and
 *       limitations under the License.
 */

import java.util.Random;

/**
 * Since tests require proper determinism, we will wrap our generation of random numbers
 * such that we have proper control.
 * Created by kevinmclellan on 07/10/2016.
 */
public class RandomGenerator {
    private static NextDouble gen;
    private static final String TEST_SEQ_KEY = "com.polydeucesys.kaos.test.rand.seq";

    static {
        init();
    }

    static void init(){
        String testSeqStr = System.getProperty(TEST_SEQ_KEY,"");
        if(testSeqStr.isEmpty()){
            gen = new SimpleRandom();
        }else{
            gen = new SequenceGenerator(testSeqStr);
        }
        gen.init();
    }

    private interface NextDouble{
        void init();
        double nextDouble();
    }

    private static class SequenceGenerator implements NextDouble{
        private double[] sequence;
        private int index= 0;

        SequenceGenerator( double[] sequence ){
            this.sequence = sequence;
        }

        SequenceGenerator( String seqStr ){

        }

        public void init(){
            String testSeqStr = System.getProperty(TEST_SEQ_KEY,"");
            setupSequence(testSeqStr);
            index = 0;
        }

        private void setupSequence( String seqStr ){
            String[] seqStrs = seqStr.split(",");
            double[] seq = new double[seqStrs.length];
            int index = 0;
            for(String s : seqStrs){
                seq[index++] = Double.parseDouble(s);
            }
            this.sequence = seq;
        }

        public double nextDouble(){
            index = index % sequence.length;
            return sequence[index++];
        }
    }

    private static class SimpleRandom implements NextDouble{
        private final Random r = new Random(System.currentTimeMillis());

        @Override
        public void init(){
            r.setSeed(System.currentTimeMillis());
        }

        public double nextDouble(){
            return r.nextDouble();
        }
    }

    public static double nextDouble(){
        return gen.nextDouble();
    }

    public static double nextDouble(double max){
        return gen.nextDouble() * max;
    }


    public static float nextFloat(){
        return (float)(gen.nextDouble());
    }
    /**
     * Return the next float in the range from 0.0 to range.
     * @param max Top value in randon result range
     * @return a value from 0.0 to max
     */
    public static float nextFloat(float max){
        return (float)(gen.nextDouble() * max);
    }

    /**
     * Return an integer in the range 0 - max. Max can be negative.
     * @param max - top value in range
     * @return a random integer in the range 0 to max
     */
    public static int nextInt( int max ){
        int sign = max < 0?-1:1;
        // 0 and max would actually have 1/2 the chance of the rest of the #
        // after rounding. so extend the range by 1 and make max + 1 be 0
        // BTW yes, Random.nextInt(trange) would also work. And
        // is safer since the - and pos + ints are
        // asymmetric (max - != max +'ve). But to allow testing
        // this uses a wrapped generator of randomness that only
        // provides floats.
        int trange = Math.abs(max) + 1;
        int res = Math.round((float)gen.nextDouble() * trange);
        return sign * (res % trange);
    }

    public static long nextLong( long max ){
        long sign = max < 0?-1:1;
        long trange = Math.abs(max) + 1;
        long res = Math.round(gen.nextDouble() * trange);
        return sign * (res % trange);
    }
}
