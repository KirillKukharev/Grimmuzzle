package com.akvelon.grimmuzzle.ui.screens.creationscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.data.entities.Attribute
import com.akvelon.grimmuzzle.data.entities.event.EventProcessor
import com.akvelon.grimmuzzle.databinding.CreationScreenFragmentBinding
import com.akvelon.grimmuzzle.ui.MainActivity
import com.google.android.material.tabs.TabLayoutMediator


class CreationScreenFragment : Fragment(),
    EventProcessor<MutableList<Attribute>> {
    private lateinit var viewModel: CreationScreenViewModel
    private lateinit var binding: CreationScreenFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.creation_screen_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreationScreenViewModel::class.java)
        binding.viewModel = viewModel
        initAttributes()
        binding.arrowBack.setOnClickListener {
            MainActivity.instance.getProgressBar().visibility = View.INVISIBLE
            MainActivity.instance.hideErrorView()
            findNavController().popBackStack()
        }
    }

    private fun initAttributes() {
        viewModel.initAttributesByType(
            this,
            this
        )
    }

    override fun onError(t: Throwable?) {
        binding.goButton.visibility = View.GONE
        binding.viewpager.visibility = View.INVISIBLE
        binding.tabs.visibility = View.GONE
        MainActivity.instance.showErrorView(::initAttributes)
    }

    override fun onLoad() {
        binding.arrowBack.visibility = View.VISIBLE
        binding.tabs.visibility = View.GONE
        binding.viewpager.visibility = View.INVISIBLE
        binding.goButton.visibility = View.GONE
        MainActivity.instance.getProgressBar().visibility = View.VISIBLE
    }

    override fun onSuccess(result: MutableList<Attribute>?) {
        result?.let { attrs ->
            attrs.forEach {
                it.isSelected.set(false)
            }
            viewModel.attributesPagesAdapter.setTabs(attrs)
            binding.goButton.setOnClickListener {
                val bundle = Bundle()
                val ids = hashMapOf<String, MutableList<Int>>()
                val selectedAttributes = attrs.filter { it.isSelected.get() }
                selectedAttributes.forEach { attr ->
                    if (ids[attr.getType().getLabel()] == null) {
                        ids[attr.getType().getLabel()] = mutableListOf()
                        ids[attr.getType().getLabel()]!!.add(attr.getID())
                    } else {
                        ids[attr.getType().getLabel()]!!.add(attr.getID())
                    }
                }
                bundle.putSerializable("input", ids)
                if (ids.size != viewModel.attributesPagesAdapter.itemCount) {
                    Toast.makeText(
                        this.context,
                        "Not all parameters was chosen",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    findNavController().navigate(
                        R.id.action_creationScreenFragment_to_readingScreenFragment,
                        bundle
                    )
                }
            }
            setupAttributePageAdapter(attrs)
        }

        binding.goButton.visibility = View.VISIBLE
        binding.viewpager.visibility = View.VISIBLE
        binding.tabs.visibility = View.VISIBLE
        MainActivity.instance.getProgressBar().visibility = View.INVISIBLE
    }

    private fun setupAttributePageAdapter(
        attributes: MutableList<Attribute>
    ) {
        var currentTab = 0
        var shouldIgnorePageChanged = false
        val viewpager = binding.viewpager
        viewpager.adapter = viewModel.attributesPagesAdapter
        val tabs = binding.tabs
        val titles = mutableListOf<String>()
            attributes.forEach {
                if (!titles.contains(it.getType().getLabel())){
                    titles.add(it.getType().getLabel())
                }
            }
        val mediator = TabLayoutMediator(tabs, viewpager) { tab, position ->
            tab.text = titles[position]
            if (attributes.find { it.getType().getTabIndex() == position && it.isSelected.get()} != null) {
                tab.view.setBackgroundResource(R.drawable.selected_attribute_type)
            } else {
                tab.view.setBackgroundResource(R.color.new_tale_button_transparent)
            }
            viewpager.currentItem = tab.position
        }
        viewpager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(i: Int) {
                    super.onPageSelected(i)
                    if (shouldIgnorePageChanged || currentTab == i) {
                        shouldIgnorePageChanged = false
                    } else {
                        currentTab = i
                    }
                }
            })
        mediator.attach()
        viewModel.attributesPagesAdapter.setReattachCallback {
            if (mediator.isAttached) {
                mediator.detach()
            }
            if (currentTab != 0) {
                shouldIgnorePageChanged = true
            }
            mediator.attach()
            viewpager.currentItem = currentTab
        }
    }
}
