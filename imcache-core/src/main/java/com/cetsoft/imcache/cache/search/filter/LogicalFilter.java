/*
* Copyright (C) 2015 Cetsoft, http://www.cetsoft.com
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
* 
* Author : Yusuf Aytas
* Date   : Nov 8, 2013
*/
package com.cetsoft.imcache.cache.search.filter;

/**
 * The Class LogicalFilter.
 */
public abstract class LogicalFilter implements Filter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cetsoft.imcache.cache.search.filter.Filter#and(com.cetsoft.imcache
	 * .cache.search.filter.Filter)
	 */
	public Filter and(Filter filter) {
		return new AndFilter(this, filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cetsoft.imcache.cache.search.filter.Filter#or(com.cetsoft.imcache
	 * .cache.search.filter.Filter)
	 */
	public Filter or(Filter filter) {
		return new OrFilter(this, filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cetsoft.imcache.cache.search.filter.Filter#diff(com.cetsoft.imcache
	 * .cache.search.filter.Filter)
	 */
	public Filter diff(Filter filter) {
		return new DiffFilter(this, filter);
	}

}
