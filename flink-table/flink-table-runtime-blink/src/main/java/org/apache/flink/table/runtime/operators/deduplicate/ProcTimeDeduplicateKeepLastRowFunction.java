/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.runtime.operators.deduplicate;

import org.apache.flink.table.data.RowData;
import org.apache.flink.table.runtime.typeutils.InternalTypeInfo;
import org.apache.flink.util.Collector;

import static org.apache.flink.table.runtime.operators.deduplicate.DeduplicateFunctionHelper.processLastRowOnChangelog;
import static org.apache.flink.table.runtime.operators.deduplicate.DeduplicateFunctionHelper.processLastRowOnProcTime;

/**
 * This function is used to deduplicate on keys and keeps only last row.
 */
public class ProcTimeDeduplicateKeepLastRowFunction
		extends DeduplicateFunctionBase<RowData, RowData, RowData, RowData> {

	private static final long serialVersionUID = -291348892087180350L;
	private final boolean generateUpdateBefore;
	private final boolean generateInsert;
	private final boolean inputIsInsertOnly;

	public ProcTimeDeduplicateKeepLastRowFunction(
			InternalTypeInfo<RowData> typeInfo,
			long minRetentionTime,
			boolean generateUpdateBefore,
			boolean generateInsert,
			boolean inputInsertOnly) {
		super(typeInfo, null, minRetentionTime);
		this.generateUpdateBefore = generateUpdateBefore;
		this.generateInsert = generateInsert;
		this.inputIsInsertOnly = inputInsertOnly;
	}

	@Override
	public void processElement(RowData input, Context ctx, Collector<RowData> out) throws Exception {
		if (inputIsInsertOnly) {
			processLastRowOnProcTime(input, generateUpdateBefore, generateInsert, state, out);
		} else {
			processLastRowOnChangelog(input, generateUpdateBefore, state, out);
		}
	}
}
