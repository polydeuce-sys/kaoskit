package com.polydeucesys.kaos.example.aspect;
/*
 * Copyright (c) 2017 Polydeuce-Sys Ltd
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

import com.polydeucesys.kaos.core.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Provides sample pointcuts and aspects for running with the StringSharer.
 * There are 2 pointcuts, one of which is associated to a Modifier, which changes the
 * format of the string message received from another cluster node or client, adding a field
 * to the JSON, and a second which can throw an exception on writing to the other servers.
 *
 * Created by kevinmclellan on 10/02/2017.
 */
@Aspect
public class StringSharerAspects {
    /*
     * This pointcut will be used to add a modifer. The modified will simulate a change to
     * the message format. For example if a 3rd party service was called by our code, and the 3rd
     * party changed thier format in some way.
     *
     * We can make pointcuts pretty general, since the use of aop.xml enables us to limit which
     * classes are being woven for this example. For a larger example, you want to be
     * knowledgable about writing pointcut specifications
     *
     */
    @Pointcut(
            value = "execution (* processMessage(String))" +
                    "&& args(msg)",
            argNames = "msg")
    public void readLinePointcut(String msg){

    }

    @Around("readLinePointcut(msg)")
    public Object modifyMessage( ProceedingJoinPoint pjp, String msg ) throws Throwable{
        Strategy<String> strategy = ConfigurationFactory.getInstance().getConfiguration().strategyForName("msg-aspect");
        for(Behaviour before : strategy.beforeBehaviours()){
            before.execute();
        }
        String work = msg;
        Object[] args = pjp.getArgs();
        for(Modifier<String> m : strategy.modifiers()){
            work = m.modify(work);
        }
        args[0] = work;
        Object res = pjp.proceed(args);
        for(Behaviour after : strategy.afterBehaviours()){
            after.execute();
        }
        return res;
    }

    /*
     * Loggers of course can't do the exception throwing, because the logging framework itself
     * will catch checked exceptions and this will prevent them from propagaint up the stace.
     * But, using aspects we can actually throw exceptions if we want.
     *
     * Note for aspects, you can pointcut a call to a class you are not weaving, but not an
     * execution!
     */

    @Pointcut(value="call (* java.io.PrintWriter.println(java.lang.String))" +
            "&& args(msg)",
    argNames = "msg")
    public void writeSocketPointcut(String msg){}

    @Around("writeSocketPointcut(msg)")
    public Object handleBehaviour(ProceedingJoinPoint pjp, String msg) throws IOException{
        Strategy<String> strategy = ConfigurationFactory.getInstance().getConfiguration().strategyForName("write-aspect");
        for(Behaviour<String> b : strategy.beforeBehaviours()){
            try {
                b.execute();
            } catch (IOException ioe) {
                throw ioe;
            }catch( Exception e){
                // noop. Why are we throwing something that doesn't make sense?
            }
        }
        try{
            return pjp.proceed();
        }catch( Throwable e){
            if(e instanceof RuntimeException) throw (RuntimeException)e;
            return null;
        }
    }
    

}
