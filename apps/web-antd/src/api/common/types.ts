/**
 * 分页查询参数。
 */
export interface PageParams {
  pageNo: number;
  pageSize: number;
}

/**
 * 通用分页结果。
 */
export interface PageResult<T> {
  list: T[];
  total: number;
}
