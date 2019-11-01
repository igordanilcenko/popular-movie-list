package com.ihardanilchanka.sampleapp.presentation.misc

import androidx.lifecycle.LiveData

class StatefulLiveData<T> : LiveData<Pair<T?, StatefulLiveData.State>>() {

    fun setData(newData: T, newState: State.Type = State.Type.READY) {
        super.setValue(Pair(newData, State(newState)))
    }

    fun postData(newData: T, newState: State.Type = State.Type.READY) {
        super.postValue(Pair(newData, State(newState)))
    }

    fun setState(newState: State.Type) {
        super.setValue(Pair(getData(), State(newState)))
    }

    fun postState(newState: State.Type) {
        super.postValue(Pair(getData(), State(newState)))
    }

    fun setErrorState(error: Throwable) {
        super.setValue(Pair(getData(), ErrorState(error)))
    }

    fun postErrorState(error: Throwable) {
        super.postValue(Pair(getData(), ErrorState(error)))
    }

    fun getData(): T? = value?.first

    fun getState(): State? = value?.second

    open class State(val type: Type) {

        enum class Type {
            READY, LOADING, ERROR, EMPTY
        }

        override fun equals(other: Any?): Boolean {
            if (other is State) {
                return type == other.type
            }
            return false
        }

        override fun toString(): String {
            return type.toString()
        }

        override fun hashCode(): Int {
            return type.hashCode()
        }
    }

    class ErrorState(val error: Throwable) : State(type = Type.ERROR)
}