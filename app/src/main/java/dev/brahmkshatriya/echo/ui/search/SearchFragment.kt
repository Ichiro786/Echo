package dev.brahmkshatriya.echo.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.brahmkshatriya.echo.data.models.Track
import dev.brahmkshatriya.echo.databinding.FragmentSearchBinding
import dev.brahmkshatriya.echo.ui.adapters.ClickListener
import dev.brahmkshatriya.echo.ui.adapters.MediaItemsContainerAdapter
import dev.brahmkshatriya.echo.ui.adapters.SearchHeaderAdapter
import dev.brahmkshatriya.echo.ui.player.PlayerBackButtonHelper
import dev.brahmkshatriya.echo.ui.player.PlayerViewModel
import dev.brahmkshatriya.echo.ui.utils.observeFlow
import dev.brahmkshatriya.echo.ui.utils.updatePaddingWithSystemInsets

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        PlayerBackButtonHelper.addCallback(this) {
            if (!it) binding.catSearchView.hide()
        }

        updatePaddingWithSystemInsets(binding.catRecyclerView)

        val header = SearchHeaderAdapter(searchViewModel.query) {
            binding.catSearchView.setupWithSearchBar(it)
        }

        binding.catSearchView.editText.setOnEditorActionListener { textView, _, _ ->
            textView.text.toString().ifBlank { null }?.let {
                searchViewModel.search(it)
                header.setText(it)
                binding.catSearchView.hide()
            }
            false
        }
        val adapter = MediaItemsContainerAdapter(lifecycle, object : ClickListener<Track> {
            override fun onClick(item: Track) {
                playerViewModel.play(item)
            }

            override fun onLongClick(item: Track) {
                playerViewModel.addToQueue(item)
            }
        })
        binding.catRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.catRecyclerView.adapter = ConcatAdapter(header, adapter)
        searchViewModel.result.observeFlow(viewLifecycleOwner) {
            if (it != null)
                adapter.submitData(it)
        }
    }
}