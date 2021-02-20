package com.zh.android.chat.service.db.web.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.zh.android.chat.service.db.web.entity.WebCollectEntity;

import java.util.List;

/**
 * @author wally
 * @date 2021/02/07
 * Web浏览收藏表Dao
 */
@Dao
public interface WebCollectDao {
    /**
     * 增加一个收藏
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveCollect(WebCollectEntity entity);

    /**
     * 更新一个收藏
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCollect(WebCollectEntity entity);

    /**
     * 删除一个收藏
     */
    @Delete
    void deleteCollect(WebCollectEntity entity);

    /**
     * 根据id，获取收藏信息
     *
     * @param id 收藏记录的主键id
     */
    @Query("SELECT * FROM tb_web_collect WHERE id=:id")
    WebCollectEntity getCollectById(int id);

    /**
     * 根据url，查找收藏记录
     *
     * @param url 网页url
     */
    @Query("SELECT * FROM tb_web_collect WHERE user_id=:userId AND url=:url")
    WebCollectEntity getCollectByUrl(String userId, String url);

    /**
     * 获取某个用户Id，收藏记录列表
     *
     * @param userId 用户Id
     */
    @Query("SELECT * FROM tb_web_collect WHERE user_id=:userId ORDER BY id DESC")
    List<WebCollectEntity> getCollectList(String userId);
}