package live.lingting.framework.mybatis.extend;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import live.lingting.framework.api.PaginationParams;
import live.lingting.framework.api.PaginationResult;
import live.lingting.framework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingting 2022/9/26 17:07
 */
public interface ExtendMapper<T> extends BaseMapper<T> {

	default Page<T> toIpage(PaginationParams params) {
		Page<T> page = new Page<>();
		page.setCurrent(params.getPage());
		page.setSize(params.getSize());

		List<PaginationParams.Sort> sorts = params.getSorts();
		if (!CollectionUtils.isEmpty(sorts)) {
			ArrayList<OrderItem> orders = new ArrayList<>();

			for (PaginationParams.Sort sort : sorts) {
				OrderItem item = new OrderItem();
				item.setAsc(!sort.getDesc());
				item.setColumn(sort.getField());
				orders.add(item);
			}

			page.setOrders(orders);
		}

		return page;
	}

	default PaginationResult<T> convert(IPage<T> iPage) {
		return new PaginationResult<>(iPage.getTotal(), iPage.getRecords());
	}

	default PaginationResult<T> selectPage(PaginationParams limit, Wrapper<T> queryWrapper) {
		Page<T> iPage = toIpage(limit);
		Page<T> tPage = selectPage(iPage, queryWrapper);
		return convert(tPage);
	}

}
