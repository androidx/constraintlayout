package androidx.constraintlayout.validation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class CLBugFragment_443(fragmentId: Int) : Fragment() {
    val fragmentId = fragmentId

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(fragmentId, container, false)
    }
}