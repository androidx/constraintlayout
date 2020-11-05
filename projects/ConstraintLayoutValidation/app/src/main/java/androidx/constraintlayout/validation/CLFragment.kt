package androidx.constraintlayout.validation

import android.os.Build
import android.os.Bundle
import android.support.constraintlayout.validation.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment

class CLBugFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_check_397, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var frameLayout = view.parent as ViewGroup
        var constraintLayout = frameLayout.parent as ViewGroup
        constraintLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        ViewCompat.setOnApplyWindowInsetsListener(constraintLayout) { v, insets ->
            frameLayout.setPadding(0, 500, 0, 0)
            insets
        }
        ViewCompat.requestApplyInsets(constraintLayout)
    }
}