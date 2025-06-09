// Get parameters
 const playerType = '$player$';
 const hlsId = '$hlsId$';
 const clientId = '$clientId$';
 const tracksParam = 'English,Español,Français,Português,Deutsch,普通话,Tiếng Việt';

 // Language mapping
 const languageMap = {
     'en': 'English',
     'es': 'Español',
     'fr': 'Français',
     'pt': 'Português',
     'de': 'Deutsch',
     'zh': '普通话',
     'vi': 'Tiếng Việt'
 };

 // Parse custom audio track labels
 let customLabels = ['English', 'Español', 'Français', 'Português', 'Deutsch', '普通话', 'Tiếng Việt'];
 if (tracksParam) {
     try {
         const trackCodes = tracksParam.split(',').map(code => code.trim());
         customLabels = trackCodes.map(code => languageMap[code] || code);
     } catch (error) {
         console.warn('Error parsing tracks parameter, using default labels:', error);
     }
 }

 // Validate inputs and build URL
 let videoUrl = '';
 let isLiveStream = false;

 if (!playerType || !['bunny', 'castr'].includes(playerType)) {
     document.getElementById('video-container').innerHTML = '<div class="error-message">Invalid player_type. Must be "bunny" or "castr".</div>';
 }
 if (playerType === 'bunny') {
         videoUrl = `https://${clientId}.b-cdn.net/${hlsId}/playlist.m3u8`;
         isLiveStream = false;
     }
  else if (playerType === 'castr') {
         videoUrl = `https://stream.castr.net/${clientId}/live_${hlsId}/rewind-1800.m3u8`;
         isLiveStream = true;
 }

 if (videoUrl) {
     // Initialize video player
     (function() {
         let player;
         const dvrThreshold = 30;
         const isSafari = /^((?!chrome|android).)*safari/i.test(navigator.userAgent);

         function initializePlayer() {
             try {
                 // Player options
                 const playerOptions = {
                     fluid: true,
                     responsive: true,
                     autoplay: true,
                     liveui: isLiveStream,
                     fill: true,
                     controlBar: {
                         pictureInPictureToggle: false,
                         children: isLiveStream ? [
                             'progressControl',
                             'playToggle',
                             'volumePanel',
                             'seekToLive',
                             'customControlSpacer',
                             'qualitySelectorHls',
                             'audioTrackButton',
                             'fullscreenToggle'
                         ] : [
                             'progressControl',
                             'playToggle',
                             'volumePanel',
                             'currentTimeDisplay',
                             'timeDivider',
                             'durationDisplay',
                             'customControlSpacer',
                             'qualitySelectorHls',
                             'audioTrackButton',
                             'fullscreenToggle'
                         ]
                     },
                     userActions: {
                         doubleClick: true,
                         hotkeys: true
                     }
                 };

                 // Add live-specific options
                 if (isLiveStream) {
                     playerOptions.liveTracker = {
                         trackingThreshold: 20,
                         liveTolerance: 15
                     };
                     playerOptions.html5 = {
                         vhs: {
                             overrideNative: !isSafari
                         }
                     };
                 }

                 // Initialize player
                 player = videojs('video-player', playerOptions);

                 // Set the source
                 player.ready(function() {
                     player.src({
                         src: videoUrl,
                         type: 'application/x-mpegURL'
                     });

                     // Setup features
                     if (isLiveStream) {
                         setupDVRFunctionality();
                     }
                     setupQualitySelector();
                     setupAudioTracks();
                 });

                // Error handling
                            player.on('error', function() {
                                const error = player.error();
                                console.error('Video.js error:', error);

                                let errorMessage = 'An error occurred while loading the video.';

                                // Get the video URL currently attempted
                                const videoUrl = player.currentSrc();
                                errorMessage += ` | Video URL: ${videoUrl}`;

                                if (error && error.code) {
                                    switch (error.code) {
                                        case 1:
                                            errorMessage = 'Video loading aborted.';
                                            break;
                                        case 2:
                                            errorMessage = 'Network error occurred.';
                                            break;
                                        case 3:
                                            errorMessage = 'Video decoding failed.';
                                            break;
                                        case 4:
                                            errorMessage = 'Video format not supported.';
                                            break;
                                        default:
                                            errorMessage = 'Unknown video error.';
                                    }

                                    errorMessage += ` | Video URL: ${videoUrl}`; // Add it again if overridden above

                                    // Append the stringified error object
                                    try {
                                        errorMessage += ' | Error details: ' + JSON.stringify(error);
                                    } catch (e) {
                                        errorMessage += ' | Error details (fallback): ' + String(error);
                                    }
                                }

                                document.getElementById('video-container').innerHTML = `<div class="error-message">${errorMessage}</div>`;
                            });
                   function setMenuClickOnly(menuButtonClass) {
             // Find the menu button element
             var btn = player.el().querySelector(menuButtonClass);
             if (!btn) return;

             // Remove default Video.js hover/focus handlers
             btn.onmouseenter = null;
             btn.onmouseleave = null;
             btn.onfocus = null;
             btn.onblur = null;

             // Prevent menu from opening on hover/focus
             btn.addEventListener('mouseenter', function(e) {
                 e.stopPropagation();
                 e.preventDefault();
             }, true);
             btn.addEventListener('focus', function(e) {
                 e.stopPropagation();
                 e.preventDefault();
             }, true);

             // Toggle menu on click
             btn.addEventListener('click', function(e) {
                 e.preventDefault();
                 e.stopPropagation();
                 // Toggle menu open/close
                 btn.classList.toggle('vjs-menu-button-popup-active');
                 // Find the menu and toggle its visibility
                 var menu = btn.querySelector('.vjs-menu');
                 if (menu) {
                     menu.style.display = (menu.style.display === 'block') ? '' : 'block';
                 }
             });
         }

         // For the quality selector (plugin may use different class, adjust as needed)
         setTimeout(() => {
             setMenuClickOnly('.vjs-quality-selector');
             setMenuClickOnly('.vjs-audio-button');
         }, 500); // 500ms delay to ensure controls are rendered



             } catch (error) {
                 console.error('Player initialization error:', error);
                 document.getElementById('video-container').innerHTML = '<div class="error-message">Failed to initialize video player</div>';
             }
         }

         // DVR functionality for Castr
         function checkDVRCapability() {
             if (!player || !isLiveStream) return false;

             const seekable = player.seekable();

             if (seekable && seekable.length > 0) {
                 const dvrWindow = seekable.end(0) - seekable.start(0);
                 const hasDVR = dvrWindow > dvrThreshold;

                 if (hasDVR) {
                     player.addClass('vjs-has-dvr');
                 } else {
                     player.removeClass('vjs-has-dvr');
                 }

                 // Always keep vjs-live class for live streams to ensure button stays visible
                 if (isLiveStream) {
                     player.addClass('vjs-live');
                 }

                 return hasDVR;
             }
             return false;
         }

         function updateLiveStatus() {
             if (!player || !isLiveStream) return;

             const seekable = player.seekable();
             const currentTime = player.currentTime();

             if (seekable && seekable.length > 0) {
                 const liveEdge = seekable.end(0);
                 const timeBehindLive = liveEdge - currentTime;
                 const isAtLiveEdge = timeBehindLive < 10;

                 const liveButton = player.controlBar.getChild('seekToLive');
                 if (liveButton) {
                     if (isAtLiveEdge) {
                         liveButton.addClass('vjs-at-live-edge');
                         liveButton.removeClass('vjs-behind-live-edge');
                     } else {
                         liveButton.removeClass('vjs-at-live-edge');
                         liveButton.addClass('vjs-behind-live-edge');
                     }
                 }
             }
         }

         function setupDVRFunctionality() {
             setInterval(checkDVRCapability, 2000);
             player.on('timeupdate', updateLiveStatus);
         }

         function setupQualitySelector() {
             if (!isSafari && player.qualityLevels && typeof player.qualityLevels === 'function') {
                 const qualityLevels = player.qualityLevels();

                 qualityLevels.on('addqualitylevel', function(event) {
                     console.log('Quality level added:', event.qualityLevel);
                 });

                 player.qualitySelectorHls({
                     displayCurrentQuality: true,
                     vjsIconClass: 'vjs-icon-hd'
                 });

                 qualityLevels.on('change', function() {
                     const selectedQuality = qualityLevels[qualityLevels.selectedIndex];
                     updateHDTag(selectedQuality);
                 });

                 player.on('loadedmetadata', () => {
                     if (qualityLevels && qualityLevels.length > 0 && qualityLevels.selectedIndex !== -1) {
                         updateHDTag(qualityLevels[qualityLevels.selectedIndex]);
                     }
                 });
             } else {
                 console.warn('HLS quality selector not supported in this browser.');
             }
         }


         function updateHDTag(selectedQuality) {
             const button = document.querySelector('.vjs-quality-selector-hls');
             if (!button) return;

             const isHD = selectedQuality && selectedQuality.height && selectedQuality.height >= 720;
             let existingTag = button.querySelector('.vjs-quality-hd-tag');

             if (isHD && !existingTag) {
                 const hdTag = document.createElement('span');
                 hdTag.className = 'vjs-quality-hd-tag';
                 hdTag.textContent = 'HD';
                 button.querySelector('.vjs-menu-button').appendChild(hdTag);
             } else if (!isHD && existingTag) {
                 existingTag.remove();
             }
         }

         function setupAudioTracks() {
             player.on('loadedmetadata', function() {
                 try {
                     const audioTracks = player.audioTracks();

                     if (audioTracks && audioTracks.tracks_) {
                         audioTracks.tracks_.forEach((track, index) => {
                             if (customLabels[index]) {
                                 track.label = customLabels[index];
                             }
                         });

                         if (player.controlBar.audioTrackButton) {
                             player.controlBar.audioTrackButton.update();
                         }
                     }
                 } catch (error) {
                     console.warn('Audio tracks setup failed:', error);
                 }
             });
         }

         // Initialize when DOM is ready
         if (document.readyState === 'complete' || (document.readyState !== 'loading' && !document.documentElement.doScroll)) {
             initializePlayer();
         } else {
             document.addEventListener('DOMContentLoaded', initializePlayer);
         }
     })();
 }