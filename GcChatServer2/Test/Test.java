import java.util.ArrayList;
import java.util.List;

import com.GcChatServer.dao.ChatRecordDao;
import com.GcChatServer.enity.ChatRecord;
import com.GcChatServer.enity.RecentChat;
import com.GcChatServer.factory.ObjectFactory;

public class Test {
	public static void main(String[] args) {
		List<RecentChat> l = new ArrayList<RecentChat>();
		l.add(new RecentChat(1, 2));
		l.remove(new RecentChat(1, 2));
		System.out.println("l=" + l);
	}
}
