// Video Player Static Initialization - Load once
(function() {
    'use strict';
    console.log('Calling initializing function');
    // Create global VideoPlayerManager
    window.VideoPlayerManager = {
        // Language mapping
        languageMap: {
            'en': 'English',
            'es': 'Español',
            'fr': 'Français',
            'pt': 'Português',
            'de': 'Deutsch',
            'zh': '普通话',
            'vi': 'Tiếng Việt'
        },

        // Default configuration
        defaultConfig: {
            containerId: 'video-container',
            playerId: 'video-player',
            tracksParam: 'English,Español,Français,Português,Deutsch,普通话,Tiếng Việt',
            autoplay: true
        },

        // Current player instance
        currentPlayer: null,

        // Constants
        DVR_THRESHOLD: 30,
        IS_SAFARI: /^((?!chrome|android).)*safari/i.test(navigator.userAgent),

        // Main load video function
        loadVideo: function(config) {
            console.log('enter loadVideo');
            // Validate required parameters
            if (!config.playerType || !config.hlsId || !config.clientId) {
                throw new Error('Missing required parameters: playerType, hlsId, clientId');
            }

            // Merge config with defaults
            const finalConfig = { ...this.defaultConfig, ...config };

            try {
                // Dispose any existing player
                this.disposeCurrentPlayer();

                // Parse custom labels
                const customLabels = this.parseCustomLabels(finalConfig.tracksParam);

                // Build video URL
                const videoConfig = this.buildVideoUrl(finalConfig.playerType, finalConfig.clientId, finalConfig.hlsId);
                const videoUrl = videoConfig.url;
                const isLiveStream = videoConfig.isLive;

                console.log('Loading video:', videoUrl, 'Live:', isLiveStream);

                // Get container element
                const container = document.getElementById(finalConfig.containerId);
                if (!container) {
                    throw new Error(`Container element with ID '${finalConfig.containerId}' not found`);
                }

                // Clear any existing error messages and create video element
                container.innerHTML = `<video id="${finalConfig.playerId}" class="video-js vjs-default-skin" controls preload="auto" data-setup="{}"></video>`;


                // Get player options
                const playerOptions = this.getPlayerOptions(isLiveStream, finalConfig.autoplay);

                // Initialize player
                const player = videojs(finalConfig.playerId, playerOptions);
                this.currentPlayer = player;

                // Set the source and setup features when ready
                player.ready(() => {
                    player.src({
                        src: videoUrl,
                        type: 'application/x-mpegURL'
                    });

                    // Setup features
                    if (isLiveStream) {
                        this.setupDVRFunctionality(player, isLiveStream);
                    }
                    this.setupQualitySelector(player);
                    this.setupAudioTracks(player, customLabels);

                    // Set menu click-only behavior after controls are rendered
                    setTimeout(() => {
                        this.setMenuClickOnly(player, '.vjs-quality-selector');
                        this.setMenuClickOnly(player, '.vjs-audio-button');
                    }, 500);
                });

                // Setup error handling
                this.setupErrorHandling(player, container);

                return player;

            } catch (error) {
                console.error('Video loading error:', error);
                const container = document.getElementById(finalConfig.containerId);
                if (container) {
                    container.innerHTML = `<div class="error-message">${error.message}</div>`;
                }
                throw error;
            }
        },

        // Get player options based on stream type
        getPlayerOptions: function(isLiveStream, autoplay) {
            const playerOptions = {
                fluid: true,
                responsive: true,
                autoplay: autoplay,
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
                        overrideNative: !this.IS_SAFARI
                    }
                };
            }

            return playerOptions;
        },

        // Setup comprehensive error handling
        setupErrorHandling: function(player, container) {
            player.on('error', function() {
                const error = player.error();
                console.error('Video.js error:', error);

                let errorMessage = 'An error occurred while loading the video.';
                const currentVideoUrl = player.currentSrc();
                errorMessage += ` | Video URL: ${currentVideoUrl}`;

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

                    errorMessage += ` | Video URL: ${currentVideoUrl}`;

                    // Append the stringified error object
                    try {
                        errorMessage += ' | Error details: ' + JSON.stringify(error);
                    } catch (e) {
                        errorMessage += ' | Error details (fallback): ' + String(error);
                    }
                }

                container.innerHTML = `<div class="error-message">${errorMessage}</div>`;
            });
        },

        // Utility function to parse custom labels
       parseCustomLabels: function(tracksParam) {
           if (!tracksParam) {
               return ['English', 'Español', 'Français', 'Português', 'Deutsch', '普通话', 'Tiếng Việt'];
           }

           try {
               const trackItems = tracksParam.split(',').map(code => code.trim());

               // Heuristic: If all items are in the languageMap keys, we assume they're language codes
               const isCodeList = trackItems.every(code => this.languageMap[code]);

               if (isCodeList) {
                   // Convert codes to display labels
                   return trackItems.map(code => this.languageMap[code]);
               } else {
                   // Already full labels, use as-is
                   return trackItems;
               }
           } catch (error) {
               console.warn('Error parsing tracksParam, using default labels:', error);
               return ['English', 'Español', 'Français', 'Português', 'Deutsch', '普通话', 'Tiếng Việt'];
           }
       },

        // Build video URL based on player type
        buildVideoUrl: function(playerType, clientId, hlsId) {
            if (!playerType || !['bunny', 'castr'].includes(playerType)) {
                throw new Error('Invalid player_type. Must be "bunny" or "castr".');
            }

            if (playerType === 'bunny') {
                return {
                    url: `https://${clientId}.b-cdn.net/${hlsId}/playlist.m3u8`,
                    isLive: false
                };
            } else if (playerType === 'castr') {
                return {
                    url: `https://stream.castr.net/${clientId}/live_${hlsId}/rewind-1800.m3u8`,
                    isLive: true
                };
            }
        },

        // Set menu to click-only (no hover)
        setMenuClickOnly: function(player, menuButtonClass) {
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
        },

        // DVR functionality for live streams
        checkDVRCapability: function(player, isLiveStream) {
            if (!player || !isLiveStream) return false;

            const seekable = player.seekable();

            if (seekable && seekable.length > 0) {
                const dvrWindow = seekable.end(0) - seekable.start(0);
                const hasDVR = dvrWindow > this.DVR_THRESHOLD;

                if (hasDVR) {
                    player.addClass('vjs-has-dvr');
                } else {
                    player.removeClass('vjs-has-dvr');
                }

                // Always keep vjs-live class for live streams
                if (isLiveStream) {
                    player.addClass('vjs-live');
                }

                return hasDVR;
            }
            return false;
        },

        updateLiveStatus: function(player, isLiveStream) {
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
        },

        setupDVRFunctionality: function(player, isLiveStream) {
            const self = this;
            setInterval(() => self.checkDVRCapability(player, isLiveStream), 2000);
            player.on('timeupdate', () => self.updateLiveStatus(player, isLiveStream));
        },

        updateHDTag: function(selectedQuality) {
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
        },

        setupQualitySelector: function(player) {
            if (!this.IS_SAFARI && player.qualityLevels && typeof player.qualityLevels === 'function') {
                const qualityLevels = player.qualityLevels();
                const self = this;

                qualityLevels.on('addqualitylevel', function(event) {
                    console.log('Quality level added:', event.qualityLevel);
                });

                player.qualitySelectorHls({
                    displayCurrentQuality: true,
                    vjsIconClass: 'vjs-icon-hd'
                });

                qualityLevels.on('change', function() {
                    const selectedQuality = qualityLevels[qualityLevels.selectedIndex];
                    self.updateHDTag(selectedQuality);
                });

                player.on('loadedmetadata', () => {
                    if (qualityLevels && qualityLevels.length > 0 && qualityLevels.selectedIndex !== -1) {
                        self.updateHDTag(qualityLevels[qualityLevels.selectedIndex]);
                    }
                });
            } else {
                console.warn('HLS quality selector not supported in this browser.');
            }
        },

        setupAudioTracks: function(player, customLabels) {
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
        },

        // Dispose current player if exists
        disposeCurrentPlayer: function() {
            if (this.currentPlayer) {
                try {
                    this.currentPlayer.dispose();
                } catch (error) {
                    console.warn('Error disposing player:', error);
                }
                this.currentPlayer = null;
            }
        }
    };

    console.log('VideoPlayerManager initialized');
})();

// Video Player Dynamic Loading - Simple function call with minimal parameters
function loadVideo(playerType, hlsId, clientId, tracksParam) {
    // Check if VideoPlayerManager is available
    if (!window.VideoPlayerManager) {
        throw new Error('VideoPlayerManager not found. Please load the static initialization script first.');
    }

    // Call the static manager with parameters
    return window.VideoPlayerManager.loadVideo({
        playerType: playerType,
        hlsId: hlsId,
        clientId: clientId,
        tracksParam: tracksParam
    });
}

// Alternative function with object parameter for backward compatibility
function loadVideoWithConfig(config) {
    console.log('enter loadVideoWithConfig');
    if (!window.VideoPlayerManager) {
        throw new Error('VideoPlayerManager not found. Please load the static initialization script first.');
    }

    return window.VideoPlayerManager.loadVideo(config);
}


function safeOnLoad(callback) {
    console.log('enter safeOnLad');
    function tryInit() {
        // Check if VideoPlayerManager (or any other required global) is ready
        if (window.VideoPlayerManager && typeof window.VideoPlayerManager.loadVideo === 'function') {
            callback(); // Safe to call your onLoad logic now
        } else {
            // Retry after delay if not yet ready
            setTimeout(tryInit, 50);
        }
    }

    if (document.readyState === 'complete') {
        tryInit();
    } else {
        window.addEventListener('load', tryInit); // Run after all resources (scripts, styles, etc.) are loaded
    }
    console.log('leave safeOnLad');
}
