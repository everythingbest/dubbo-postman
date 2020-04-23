/*
 * MIT License
 *
 * Copyright (c) 2019 everythingbest
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.rpcpostman.service.registry.impl;

import com.rpcpostman.service.registry.Register;
import com.rpcpostman.service.registry.RegisterFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author everythingbest
 */
public abstract class AbstractRegisterFactory implements RegisterFactory {

    protected Map<String, Register> allRegisters = new HashMap<>();

    protected Set<String> clusterSet = new HashSet<>();

    @Override
    public void addCluster(String cluster) {
        clusterSet.add(cluster);
    }

    public Register get(String cluster){
        if(allRegisters.containsKey(cluster)){
            return allRegisters.get(cluster);
        }
        Register register = create(cluster);
        allRegisters.put(cluster,register);
        return register;
    }

    public Register remove(String cluster){
        return allRegisters.remove(cluster);
    }

    public abstract Register create(String cluster);
}
