package com.akvelon.grimmuzzle.ui.screens.readingscreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.data.entities.event.EventProcessor
import com.akvelon.grimmuzzle.data.preferences.SavedTalesPreferences
import com.akvelon.grimmuzzle.databinding.ReadingScreenFragmentBinding
import com.akvelon.grimmuzzle.ui.MainActivity
import kotlin.math.abs


class ReadingScreenFragment : Fragment(), EventProcessor<Any> {

    private lateinit var viewModel: ReadingScreenViewModel
    private lateinit var binding: ReadingScreenFragmentBinding

    private val MIN_DISCTANCE = 150f
    private val MIN_TAP_HEIGHT_COEFFICIENT = 0.25f
    private val MAX_TAP_HEIGHT_COEFFICIENT = 0.75f
    private val MAX_TAP_WIDTH_COEFFICIENT = 0.66f
    private val MIN_TAP_WIDTH_COEFFICIENT = 0.33f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.reading_screen_fragment, container, false)
        return binding.root
    }

    private fun onNext() {
        viewModel.leafPageForward()
        refreshNavigationControls()
    }

    private fun onPrevious() {
        viewModel.leafPageBack()
        refreshNavigationControls()
    }

    private fun refreshNavigationControls() {
        if (viewModel.currTextChunkIndex.get()!! == 0) {
            binding.arrowLeft.visibility = View.INVISIBLE
        } else {
            binding.arrowLeft.visibility = View.VISIBLE
        }
        if (viewModel.currTextChunkIndex.get()!! < viewModel.textChunksList.lastIndex)
            binding.arrowRight.visibility = View.VISIBLE
        else
            binding.arrowRight.visibility = View.INVISIBLE
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReadingScreenViewModel::class.java)
        binding.viewmodel = viewModel
        //Tale parameters from CreationScreenFragment
        initTaleFromArguments()
        binding.arrowBack.setOnClickListener {
            MainActivity.instance.hideErrorView()
            MainActivity.instance.getProgressBar().visibility = View.INVISIBLE
            findNavController().popBackStack(R.id.mainScreenFragment, false)
        }
    }

    private fun initTaleFromArguments() {
        var id: String? = null
        val title = "Новая сказка"
        var length = 250
        var input = hashMapOf<String, List<Int>>()
        arguments?.let {
            id = it.getString("id", null)
            length = it.getInt("inputLength", 250)
            if (it.getSerializable("input") != null)
                input = it.getSerializable("input") as HashMap<String, List<Int>>
        }
        if (id != null)
            viewModel.loadExistingTale(
                id!!,
                this,
                this
            )
        else
            viewModel.createTale(
                title,
                input, length, this,
                this
            )
    }

    private fun setupGestureListener() {
        var x1 = 0f
        var x2: Float
        var y1 = 0f
        var y2: Float
        val screenHeight = resources.displayMetrics.heightPixels
        val screenWidth = resources.displayMetrics.widthPixels

        binding.root.setOnTouchListener(View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                x1 = event.x
                y1 = event.y
                return@OnTouchListener true
            } else if (event.action == MotionEvent.ACTION_UP) {
                x2 = event.x
                y2 = event.y
                val deltaX = x2 - x1
                val deltaY = y2 - y1
                if (abs(deltaX) > MIN_DISCTANCE) {
                    if (x2 > x1) {
                        onPrevious()
                    } else {
                        onNext()
                    }
                    x1 = 0f
                    x2 = 0f
                    binding.root.performClick()
                    return@OnTouchListener true
                } else if (deltaX == 0F && deltaY == 0F) {
                    if (MAX_TAP_HEIGHT_COEFFICIENT * screenHeight >= y1 && y1 >= MIN_TAP_HEIGHT_COEFFICIENT * screenHeight) {
                        if (x1 >= MAX_TAP_WIDTH_COEFFICIENT * screenWidth) {
                            onNext()
                        } else if (x1 <= MIN_TAP_WIDTH_COEFFICIENT * screenWidth) {
                            onPrevious()
                        }
                    }
                    x1 = 0F
                    x2 = 0F
                    return@OnTouchListener true
                } else {
                    return@OnTouchListener false
                }
            } else {
                return@OnTouchListener false
            }
        })
    }

    override fun onLoad() {
        binding.arrowBack.visibility = View.VISIBLE
        MainActivity.instance.getProgressBar().visibility = View.VISIBLE
        binding.taleImage.visibility = View.VISIBLE
        binding.arrowLeft.visibility = View.INVISIBLE
        binding.arrowRight.visibility = View.INVISIBLE
        binding.saveTale.visibility = View.INVISIBLE
        binding.editTale.visibility = View.INVISIBLE
        binding.shareTale.visibility = View.INVISIBLE
    }

    override fun onSuccess() {
        if (viewModel.tale.get() != null) {
            viewModel.textChunksList = getPreparedTextChunks(
                binding.taleText,
                viewModel.tale.get()?.content!!
            )
            binding.arrowLeft.setOnClickListener {
                onPrevious()
            }
            binding.arrowRight.setOnClickListener {
                onNext()
            }
            showElementsOnContentLoaded()
            setupGestureListener()
            setupSaveTaleListener()
            setupShareTaleListener()
            setupEditButtonListener()
        }
    }

    override fun onError(t: Throwable?) {
        hideElementsOnErrorLoaded()
        MainActivity.instance.showErrorView(::initTaleFromArguments)
        MainActivity.instance.getProgressBar().visibility = View.VISIBLE
    }

    private fun setupEditButtonListener() {
        val id = viewModel.tale.get()?.id.toString()
        val pref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val savedIdsString =
            StringBuilder(pref.getString(resources.getString(R.string.saved_tales_ids), "") ?: "")

        if (savedIdsString.isEmpty() || !savedIdsString.split(" ").contains(id)) {
            return
        }
        binding.editTale.visibility = View.VISIBLE

        binding.editTale.setOnClickListener {
            val renamingDialog = viewModel.renamingDialog
            val bundle = Bundle()
            bundle.putString("id", viewModel.tale.get()?.id)
            bundle.putString("name", viewModel.tale.get()?.title)
            renamingDialog.arguments = bundle
            renamingDialog.setOnResponseCallback { newName: String ->
                viewModel.tale.get()?.let { tale ->
                    tale.title = newName
                    viewModel.dataNode.forceUpdateTaleOnNextCall(
                        tale
                    )
                }
                Toast.makeText(
                    context,
                    resources.getString(R.string.tale_has_been_renamed),
                    Toast.LENGTH_SHORT
                ).show()
            }
            renamingDialog.show(this@ReadingScreenFragment.parentFragmentManager, "RenamingDialog")
        }
    }

    private fun setupShareTaleListener() {
        val id = viewModel.tale.get()?.id.toString()
        val isTaleInStore = viewModel.tale.get()?.inStore
        if (isTaleInStore == false && DataNode.hasNetwork()) {
            binding.shareTale.visibility = View.VISIBLE
        }

        binding.shareTale.setOnClickListener {
            id.let { taleId ->
                viewModel.shareTale(taleId, {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.tale_has_not_been_shared),
                        Toast.LENGTH_SHORT
                    ).show()
                }, {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.tale_has_been_shared),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.shareTale.visibility = View.INVISIBLE
                })
            }
        }
    }

    private fun setupSaveTaleListener() {
        val id = viewModel.tale.get()?.id
        SavedTalesPreferences.updateSavedIds()
        if (SavedTalesPreferences.savedIds.get()!!.contains(id)) {
            return
        }
        binding.saveTale.visibility = View.VISIBLE
        binding.saveTale.setOnClickListener {
            val renamingDialog = viewModel.renamingDialog
            val bundle = Bundle()
            bundle.putString("id", viewModel.tale.get()?.id)
            bundle.putString("name", viewModel.tale.get()?.title)
            renamingDialog.arguments = bundle
            renamingDialog.setOnResponseCallback { newName: String ->
                viewModel.tale.get()!!.title = newName
                id?.let { taleId -> SavedTalesPreferences.insertSavedId(taleId) }
                binding.saveTale.visibility = View.INVISIBLE
            }
            renamingDialog.show(this@ReadingScreenFragment.parentFragmentManager, "RenamingDialog")
        }
    }

    private fun getPreparedTextChunks(
        textView: TextView,
        text: String
    ): MutableList<String> {
        val textChunksList = mutableListOf<String>()
        var textForTale = text

        while (textForTale.isNotEmpty()) {
            textForTale = addTextToChunksList(
                textView,
                textForTale,
                textChunksList
            )
        }
        return textChunksList
    }

    //Place text chunk to the plate and returns remainder
    private fun addTextToChunksList(
        textView: TextView,
        text: String,
        textChunksList: MutableList<String>
    ): String {
        val lineMaxCount = textView.height / textView.lineHeight
        textView.text = text
        textView.onPreDraw()

        if (textView.lineCount < lineMaxCount) {
            textChunksList.add(textView.text.toString())
            return ""
        }

        val start = textView.layout.getLineStart(0)
        val end = textView.layout.getLineEnd(lineMaxCount - 1)
        textChunksList.add(text.subSequence(start, end).toString())

        return text.substring(end, text.length)
    }

    private fun showElementsOnContentLoaded() {
        binding.taleImage.visibility = View.VISIBLE
        binding.taleText.visibility = View.VISIBLE
        binding.textFrame.visibility = View.VISIBLE
        binding.arrowLeft.visibility = View.INVISIBLE
        if (viewModel.textChunksList.size > 1)
            binding.arrowRight.visibility = View.VISIBLE
        else
            binding.arrowRight.visibility = View.INVISIBLE
        MainActivity.instance.getProgressBar().visibility = View.INVISIBLE
    }

    private fun hideElementsOnErrorLoaded() {
        binding.taleText.visibility = View.INVISIBLE
        binding.textFrame.visibility = View.INVISIBLE
        binding.arrowLeft.visibility = View.INVISIBLE
        binding.arrowRight.visibility = View.INVISIBLE
        MainActivity.instance.getProgressBar().visibility = View.INVISIBLE
        binding.shareTale.visibility = View.INVISIBLE
        binding.editTale.visibility = View.INVISIBLE
    }
}