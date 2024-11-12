package live.lingting.polaris.grpc.loadbalance

import com.google.common.base.Preconditions
import com.tencent.polaris.api.pojo.CircuitBreakerStatus
import com.tencent.polaris.api.pojo.Instance
import com.tencent.polaris.api.pojo.StatusDimension
import com.tencent.polaris.api.utils.StringUtils
import io.grpc.Attributes
import io.grpc.Channel
import io.grpc.ChannelLogger
import io.grpc.EquivalentAddressGroup
import io.grpc.LoadBalancer
import io.grpc.LoadBalancer.SubchannelStateListener
import kotlin.concurrent.Volatile

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class PolarisSubChannel : LoadBalancer.Subchannel, Instance {
    val channel: LoadBalancer.Subchannel?

    @Volatile
    private var instance: Instance?

    internal constructor(instance: Instance?) {
        Preconditions.checkNotNull(instance, NLP_MESSAGE)
        this.channel = null
        this.instance = instance
    }

    constructor(channel: LoadBalancer.Subchannel?, instance: Instance?) {
        Preconditions.checkNotNull(channel, "channel")
        Preconditions.checkNotNull(instance, NLP_MESSAGE)
        this.channel = channel
        this.instance = instance
    }

    fun getInstance(): Instance? {
        return instance
    }

    fun setInstance(instance: Instance?) {
        Preconditions.checkNotNull(instance, NLP_MESSAGE)
        this.instance = instance
    }

    override fun getNamespace(): String {
        return instance!!.namespace
    }

    override fun getService(): String {
        return instance!!.service
    }

    override fun getRevision(): String {
        return instance!!.revision
    }

    override fun getCircuitBreakerStatus(): CircuitBreakerStatus {
        return instance!!.circuitBreakerStatus
    }

    override fun getStatusDimensions(): Collection<StatusDimension> {
        return instance!!.statusDimensions
    }

    override fun getCircuitBreakerStatus(statusDimension: StatusDimension): CircuitBreakerStatus {
        return instance!!.getCircuitBreakerStatus(statusDimension)
    }

    override fun isHealthy(): Boolean {
        return instance!!.isHealthy
    }

    override fun isIsolated(): Boolean {
        return instance!!.isIsolated
    }

    override fun getProtocol(): String {
        return instance!!.protocol
    }

    override fun getId(): String {
        return instance!!.id
    }

    override fun getHost(): String {
        return instance!!.host
    }

    override fun getPort(): Int {
        return instance!!.port
    }

    override fun getVersion(): String {
        return instance!!.version
    }

    override fun getMetadata(): Map<String, String> {
        return instance!!.metadata
    }

    override fun isEnableHealthCheck(): Boolean {
        return instance!!.isEnableHealthCheck
    }

    override fun getRegion(): String {
        return instance!!.region
    }

    override fun getZone(): String {
        return instance!!.zone
    }

    override fun getCampus(): String {
        return instance!!.campus
    }

    override fun getPriority(): Int {
        return instance!!.priority
    }

    override fun getWeight(): Int {
        return instance!!.weight
    }

    override fun getLogicSet(): String {
        return instance!!.logicSet
    }

    override fun shutdown() {
        channel!!.shutdown()
    }

    override fun start(listener: SubchannelStateListener) {
        channel!!.start(listener)
    }

    override fun getAllAddresses(): List<EquivalentAddressGroup> {
        return channel!!.allAddresses
    }

    override fun asChannel(): Channel {
        return channel!!.asChannel()
    }

    override fun getChannelLogger(): ChannelLogger {
        return channel!!.channelLogger
    }

    override fun updateAddresses(addrs: List<EquivalentAddressGroup>) {
        channel!!.updateAddresses(addrs)
    }

    override fun getInternalSubchannel(): Any {
        return channel!!.internalSubchannel
    }

    override fun requestConnection() {
        channel!!.requestConnection()
    }

    override fun getAttributes(): Attributes {
        return channel!!.attributes
    }

    override fun compareTo(o: Instance): Int {
        return instance!!.compareTo(o)
    }

    override fun equals(o: Any?): Boolean {
        if (o is PolarisSubChannel) {
            return StringUtils.equals(this.id, o.id)
        }
        return false
    }

    override fun hashCode(): Int {
        return getInstance()!!.id.hashCode()
    }

    companion object {
        private const val NLP_MESSAGE = "instance"
    }
}
