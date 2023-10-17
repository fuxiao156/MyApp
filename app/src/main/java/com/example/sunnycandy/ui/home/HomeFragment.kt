package com.example.sunnycandy.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sunnycandy.databinding.FragmentHomeBinding
import com.example.sunnycandy.utils.ImageUtils

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


//        处理图片
        val textView2 : TextView = binding.icon1
        textView2.text = "sunnycandy"
        val drawable = textView2.compoundDrawables
        val context: Context = requireContext()
        val scaledDrawable = ImageUtils.scaleDrawable(drawable[1], targetWidth = 180)
        textView2.setCompoundDrawablesWithIntrinsicBounds(
            null,
            scaledDrawable, // top，这里设置缩放后的 Drawable
            null,
            null,
        )
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}