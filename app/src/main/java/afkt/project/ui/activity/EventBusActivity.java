package afkt.project.ui.activity;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import afkt.project.R;
import afkt.project.base.app.BaseToolbarActivity;
import afkt.project.model.item.ButtonValue;
import afkt.project.ui.adapter.ButtonAdapter;
import butterknife.BindView;
import dev.base.DevBaseEvent;
import dev.other.EventBusUtils;
import dev.utils.app.logger.DevLogger;
import dev.utils.app.toast.ToastTintUtils;
import utils_use.toast.ToastTintUse;

/**
 * detail: EventBusUtils
 * @author Ttt
 * <pre>
 *     {@link ToastTintUse}
 * </pre>
 */
public class EventBusActivity extends BaseToolbarActivity {

    // = View =
    @BindView(R.id.vid_bvr_recy)
    RecyclerView vid_bvr_recy;

    @Override
    public int getLayoutId() {
        return R.layout.base_view_recyclerview;
    }

    @Override
    public void initValues() {
        super.initValues();

        // 初始化布局管理器、适配器
        final ButtonAdapter buttonAdapter = new ButtonAdapter(ButtonValue.getEventButtonValues());
        vid_bvr_recy.setLayoutManager(new LinearLayoutManager(this));
        vid_bvr_recy.setAdapter(buttonAdapter);
        buttonAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ButtonValue buttonValue = buttonAdapter.getItem(position);
                switch (buttonValue.type) {
                    case ButtonValue.BTN_EVENT_REGISTER:
                        showToast(true, "注册成功");
                        EventBusUtils.register(EventBusActivity.this);
                        break;
                    case ButtonValue.BTN_EVENT_UNREGISTER:
                        showToast(true, "解绑成功");
                        EventBusUtils.unregister(EventBusActivity.this);
                        break;
                    case ButtonValue.BTN_EVENT_CLEAN_STICKY:
                        showToast(true, "清空全部粘性事件成功");
                        EventBusUtils.removeAllStickyEvents();
                        break;
                    case ButtonValue.BTN_EVENT_SEND:
                        showToast(true, "发送事件成功");
                        DevBaseEvent<String> event = new DevBaseEvent<>();
                        event.setCode(1).setValue("正常消息");
                        EventBusUtils.post(event);
                        break;
                    case ButtonValue.BTN_EVENT_SEND_STICKY:
                        showToast(true, "发送粘性事件成功");
                        DevBaseEvent<String> eventSticky = new DevBaseEvent<>();
                        eventSticky.setCode(2).setValue("粘性消息");
                        EventBusUtils.postSticky(eventSticky);
                        // 如何测试粘性消息, 先注册并发送粘性事件, 然后解绑, 再次注册(则会再次接收到粘性事件消息)
                        break;
                    default:
                        ToastTintUtils.warning("未处理 " + buttonValue.text + " 事件");
                        break;
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public final void onEventBus(DevBaseEvent<String> event) {
        // 打印数据
        DevLogger.dTag(mTag, "value -> " + event.getValue());
        // 进行提示
        ToastTintUtils.normal(event.getCode() + "." + event.getValue());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public final void onEventBusSticky(DevBaseEvent<String> event) {
        // 打印数据
        DevLogger.dTag(mTag, "value -> " + event.getValue());
        // 进行提示
        ToastTintUtils.warning(event.getCode() + "." + event.getValue());

//        // 接收到后, 需要把旧的粘性事件删除 - 否则每次注册都会触发发送的粘性消息
//        EventBusUtils.removeStickyEvent(event.getClass());
    }
}