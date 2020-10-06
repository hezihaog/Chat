package com.zh.android.chat.friend.ui.fragment

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.github.promeg.pinyinhelper.Pinyin
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.ext.handlerErrorCode
import com.zh.android.base.ext.ioToMain
import com.zh.android.base.ext.lifecycle
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.friend.FriendUIHelper
import com.zh.android.chat.friend.R
import com.zh.android.chat.friend.http.FriendPresenter
import com.zh.android.chat.friend.item.FriendViewBinder
import com.zh.android.chat.friend.item.LetterViewBinder
import com.zh.android.chat.friend.model.LetterModel
import com.zh.android.chat.friend.widget.SlideBar
import com.zh.android.chat.friend.widget.SlideBar.OnSelectItemListener
import com.zh.android.chat.service.module.conversation.ConversationService
import com.zh.android.chat.service.module.login.LoginService
import com.zh.android.chat.service.module.mine.model.User
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import java.util.*

/**
 * @author wally
 * @date 2020/08/26
 * 好友模块首页
 */
class FriendMainFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    @JvmField
    @Autowired(name = ARouterUrl.CONVERSATION_SERVICE)
    var mConversationService: ConversationService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)
    private val vSlideBar: SlideBar by bindView(R.id.slide_bar)
    private val vCheckLetterView: TextView by bindView(R.id.check_letter)
    private val vFriendRequestLayout: View by bindView(R.id.friend_request_layout)
    private val vFriendRequestNum: TextView by bindView(R.id.friend_request_num)

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            //字母条目
            register(LetterModel::class.java, LetterViewBinder())
            //好友条目
            register(User::class.java, FriendViewBinder {
                //跳转到会话
                mConversationService?.goConversationChat(
                    fragmentActivity,
                    it.id, it.nickname
                )
            })
        }
    }

    private val mFriendPresenter by lazy {
        FriendPresenter()
    }

    /**
     * 用于保存联系人首字母在列表的位置
     */
    private val mLetterPositionMap = mutableMapOf<String, Int>()

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onInflaterViewId(): Int {
        return R.layout.friend_main_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            setTitle(getString(R.string.friend_module_name))
            addRightTextButton(R.string.friend_vicinity_user, R.id.friend_vicinity_user)
                .click {
                    //附近的人
                    FriendUIHelper.goVicinityUser(fragmentActivity)
                }
        }
        vRefreshList.apply {
            layoutManager = LinearLayoutManager(fragmentActivity)
            adapter = mListAdapter
        }
        //配置索引条
        vSlideBar.setOnSelectItemListener(object : OnSelectItemListener {
            override fun onItemSelect(position: Int, selectLetter: String) {
                vCheckLetterView.run {
                    if (visibility != View.VISIBLE) {
                        visibility = View.VISIBLE
                    }
                    text = selectLetter
                }
                val letterStickyPosition = mLetterPositionMap[selectLetter]
                //这里可能拿不到，因为并不是所有的字母联系人名字上都有
                if (letterStickyPosition != null) {
                    vRefreshList.scrollToPosition(letterStickyPosition)
                }
            }

            override fun onItemUnSelect() {
                vCheckLetterView.visibility = View.GONE
            }
        })
        vFriendRequestLayout.click {
            //跳转到好友申请列表
            FriendUIHelper.goFriendRequestRecord(fragmentActivity)
        }
    }

    /**
     * 刷新好友列表
     */
    private fun refresh() {
        getUserFriendList()
        getAllFriendRequest()
    }

    /**
     * 获取我的好友列表
     */
    private fun getUserFriendList() {
        mLoginService?.run {
            val userId = getUserId()
            mFriendPresenter.getUserFriendList(userId)
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({ httpModel ->
                    if (handlerErrorCode(httpModel)) {
                        httpModel.data?.let {
                            mListItems.clear()
                            mListItems.addAll(generateItems(it))
                            mListAdapter.notifyDataSetChanged()
                        }
                    }
                }, { error ->
                    error.printStackTrace()
                })
        }
    }

    /**
     * 获取我所有的好友请求
     */
    private fun getAllFriendRequest() {
        mLoginService?.run {
            val userId = getUserId()
            mFriendPresenter.getUserAllFriendRequest(userId)
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({ httpModel ->
                    if (handlerErrorCode(httpModel)) {
                        val list = httpModel.data
                        vFriendRequestLayout.visibility = if (list.isNullOrEmpty()) {
                            View.GONE
                        } else {
                            View.VISIBLE
                        }
                        //请求数量
                        val requestNum = (list?.size ?: 0).toString()
                        vFriendRequestNum.text = requestNum
                    }
                }, { error ->
                    error.printStackTrace()
                })
        }
    }

    /**
     * 生成条目
     */
    private fun generateItems(list: List<User>): List<Any> {
        //先对列表按字母排序
        Collections.sort(list) { o1, o2 ->
            val letter =
                Character.toUpperCase(Pinyin.toPinyin(o1.nickname[0])[0])
            val nextLetter =
                Character.toUpperCase(Pinyin.toPinyin(o2.nickname[0])[0])
            if (letter == nextLetter) {
                0
            } else {
                letter - nextLetter
            }
        }
        //结果列表
        val resultList = mutableListOf<Any>()
        //当前遍历到的字母
        var letter = 0.toChar()
        //遍历分组条目，相同首字母的同一组
        for (i in list.indices) {
            if (i == 0) {
                //获取文字第一个字母
                val letterPinyin = Pinyin.toPinyin(list[i].nickname[0])
                letter = Character.toUpperCase(letterPinyin[0])
                resultList.add(LetterModel(letter.toString()))
            } else {
                //如果下一个条目的首字母和上一个的不一样，则插入一条新的字母条目
                val letterPinyin = Pinyin.toPinyin(list[i].nickname[0])
                val nextLetter = Character.toUpperCase(letterPinyin[0])
                if (nextLetter != letter) {
                    letter = nextLetter
                    resultList.add(LetterModel(letter.toString()))
                    //记录字母条目的位置，后续拉动字母选择条时跳转位置
                    mLetterPositionMap[letter.toString().toUpperCase(Locale.getDefault())] =
                        resultList.size - 1
                }
            }
            //添加好友条目
            resultList.add(list[i])
        }
        return resultList
    }
}