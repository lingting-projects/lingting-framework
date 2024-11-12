
package live.lingting.polaris.grpc.loadbalance;

import com.tencent.polaris.api.exception.PolarisException;
import com.tencent.polaris.api.pojo.RetStatus;
import com.tencent.polaris.api.rpc.ServiceCallResult;
import io.grpc.ClientStreamTracer;
import io.grpc.Status;
import live.lingting.polaris.grpc.util.ClientCallInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * grpc 调用的 tracer 信息，记录每次 grpc 调用的情况 1. 每次请求的相应时间 2. 每次请求的结果，记录成功或者失败
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
@SuppressWarnings("java:S1874")
public class PolarisClientStreamTracer extends ClientStreamTracer {

	private static final Logger LOG = LoggerFactory.getLogger(PolarisClientStreamTracer.class);

	private final ClientCallInfo info;

	private final Map<Integer, Long> perStreamRequestStartTimes = new ConcurrentHashMap<>();

	private final ServiceCallResult result;

	public PolarisClientStreamTracer(ClientCallInfo callInfo) {
		this.info = callInfo;
		this.result = new ServiceCallResult();

		this.result.setHost(callInfo.getInstance().getHost());
		this.result.setPort(callInfo.getInstance().getPort());
		this.result.setMethod(callInfo.getMethod());
		this.result.setNamespace(callInfo.getTargetNamespace());
		this.result.setService(callInfo.getTargetService());
	}

	@Override
	public void outboundMessage(int seqNo) {
		perStreamRequestStartTimes.put(seqNo, System.currentTimeMillis());
	}

	@Override
	public void inboundMessage(int seqNo) {
		Long startTime = perStreamRequestStartTimes.get(seqNo);
		if (Objects.isNull(startTime)) {
			return;
		}
		this.result.setRetStatus(RetStatus.RetSuccess);
		this.result.setRetCode(Status.OK.getCode().value());
		this.result.setDelay(System.currentTimeMillis() - startTime);

		try {
			this.info.getConsumerAPI().updateServiceCallResult(result);
		}
		catch (PolarisException e) {
			LOG.error("[grpc-polaris] do report invoke call ret fail in inboundMessageRead", e);
		}
	}

}
