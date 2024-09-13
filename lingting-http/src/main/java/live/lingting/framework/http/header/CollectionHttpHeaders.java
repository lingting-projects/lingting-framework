package live.lingting.framework.http.header;

import java.util.ArrayList;

/**
 * @author lingting 2024-09-12 23:41
 */
public class CollectionHttpHeaders extends AbstractHttpHeaders implements HttpHeaders {

	public CollectionHttpHeaders() {
		super(ArrayList::new);
	}

}
