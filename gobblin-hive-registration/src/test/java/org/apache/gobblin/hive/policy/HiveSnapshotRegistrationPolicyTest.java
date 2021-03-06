/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.gobblin.hive.policy;

import org.apache.gobblin.configuration.State;
import org.apache.gobblin.hive.spec.HiveSpec;
import org.apache.hadoop.fs.Path;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import static org.apache.gobblin.hive.policy.HiveRegistrationPolicyBaseTest.examine;

@Test(groups = {"gobblin.hive"})
public class HiveSnapshotRegistrationPolicyTest {
    private Path path;

    @Test
    public void testGetHiveSpecs()
            throws IOException {
        State state = new State();
        this.path = new Path(getClass().getResource("/test-hive-table/snapshot1").toString());
        //Test when directory contain zero snapshot
        Collection<HiveSpec> specs = new HiveSnapshotRegistrationPolicy(state).getHiveSpecs(this.path);
        Assert.assertEquals(specs.size(), 0);

        //Test when directory contain snapshots sub-directory
        this.path = new Path(getClass().getResource("/test-hive-table/").toString());

        Assert.assertEquals(specs.size(), 0);
        state.appendToListProp(HiveRegistrationPolicyBase.HIVE_DATABASE_NAME, "db1");
        state.appendToListProp(HiveRegistrationPolicyBase.ADDITIONAL_HIVE_DATABASE_NAMES, "db2");

        state.appendToListProp(HiveRegistrationPolicyBase.HIVE_TABLE_NAME, "tbl1");
        state.appendToListProp(HiveRegistrationPolicyBase.ADDITIONAL_HIVE_TABLE_NAMES, "tbl2,tbl3");
        specs = new HiveSnapshotRegistrationPolicy(state).getHiveSpecs(this.path);
        Assert.assertEquals(specs.size(), 6);
        Iterator<HiveSpec> iterator = specs.iterator();
        HiveSpec spec = iterator.next();
        examine(spec, "db1", "tbl1");
        spec = iterator.next();
        examine(spec, "db1", "tbl2");
        spec = iterator.next();
        examine(spec, "db1", "tbl3");
        spec = iterator.next();
        examine(spec, "db2", "tbl1");
        spec = iterator.next();
        examine(spec, "db2", "tbl2");
        spec = iterator.next();
        examine(spec, "db2", "tbl3");
    }
}
