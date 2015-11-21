/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foxconn.lib.download;

// TODO: Auto-generated Javadoc

/**
 * 
 * © 2012 amsoft.cn
 * 名称：AbDownloadProgressListener.java 
 * 描述：下载线程监听.
 * @author 还如一梦中
 * @date：2013-10-16 下午1:33:39
 * @version v1.0
 */
public interface AbDownloadProgressListener {
	
	/**
	 * On download size.
	 *
	 * @param size the size
	 */
	public void onDownloadSize(long size);
}


