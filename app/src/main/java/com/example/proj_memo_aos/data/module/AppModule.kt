package com.example.proj_memo_aos.data.module

import android.app.Application
import android.content.Context
import com.example.proj_memo_aos.data.sharedpref.SharedPreferencesMemoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

//Hilt 라이브러리를 사용할 때 필요한 의존성을 제공하는 모듈 오브젝트
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideSharedPreferencesMemoRepository(context: Context): SharedPreferencesMemoRepository {
        return SharedPreferencesMemoRepository(context)
    }

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}