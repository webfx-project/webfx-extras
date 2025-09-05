// Video Player Static Initialization - Load once
(function() {
    'use strict';
    console.log('Calling initializing function');

    // Create global VideoPlayerManager - This is the main controller for all video player functionality
    window.VideoPlayerManager = {
        // Language mapping - Maps language codes to display names for audio tracks
        // Used when no custom track labels are provided via "tracks" Parameter
        languageMap: {
            'en': 'English',
            'es': 'Español',
            'fr': 'Français',
            'pt': 'Português',
            'de': 'Deutsch',
             //We don't put chinese (zh, because videojs can't distinguish between Mandarin and Cantonese)
             'it': 'Italiano',
             'fi': 'Suomi',
             'el': 'Ελληνικά',
             'vi': 'Tiếng Việt'
        },

        // Default configuration values for video player initialization
        defaultConfig: {
            containerId: 'video-container',
            videojsPlayerId: 'video-player',
            tracksParam: '',
            autoplay: true
        },

        // Current player instance - keeps track of the active video player
        currentPlayer: null,

        // Constants for player behavior
        DVR_THRESHOLD: 30, // Minimum seconds of seekable content to enable DVR functionality
        IS_SAFARI: /^((?!chrome|android).)*safari/i.test(navigator.userAgent), // Safari detection for HLS handling

        // Main load video function - Primary entry point for loading and configuring video players
        loadVideo: function(config) {
            return new Promise((resolve, reject) => {
                // Merge config with defaults
                const finalConfig = { ...this.defaultConfig, ...config };

                // Validate required parameters
                if (!finalConfig.playerType || !finalConfig.hlsId || !finalConfig.clientId) {
                    return reject(new Error('Missing required parameters: playerType, hlsId, clientId'));
                }

                const initPlayer = (container) => {
                    try {
                        this.disposeCurrentPlayer();
                        const customLabels = this.parseCustomLabels(finalConfig.tracksParam);
                        const videoConfig = this.buildVideoUrl(finalConfig.playerType, finalConfig.clientId, finalConfig.hlsId);
                        const videoUrl = videoConfig.url;
                        const isLiveStream = videoConfig.isLive;

                        console.log('Loading video:', videoUrl, 'Live:', isLiveStream);
                        container.innerHTML = `<video id="${finalConfig.videojsPlayerId}" class="video-js vjs-default-skin" controls preload="auto" data-setup="{}"></video>`;
                        const playerOptions = this.getPlayerOptions(isLiveStream, finalConfig.autoplay);
                        const player = videojs(finalConfig.videojsPlayerId, playerOptions);
                        this.currentPlayer = player;

                        player.ready(() => {
                            player.src({ src: videoUrl, type: 'application/x-mpegURL' });
                            if (isLiveStream) this.setupDVRFunctionality(player, isLiveStream);
                            this.setupQualitySelector(player);
                            this.setupAudioTracks(player, customLabels);
                            setTimeout(() => {
                                this.setMenuClickOnly(player, '.vjs-quality-selector');
                                this.setMenuClickOnly(player, '.vjs-audio-button');
                            }, 500);
                        });

                        this.setupErrorHandling(player, container);
                        resolve(player);
                    } catch (error) {
                        console.error('Video loading error:', error);
                        if (container) container.innerHTML = `<div class="error-message">${error.message}</div>`;
                        reject(error);
                    }
                };

                const waitForContainer = (retries = 10) => {
                    const container = document.getElementById(finalConfig.containerId);
                    if (container) {
                        initPlayer(container);
                    } else if (retries > 0) {
                        //We wait a bit for the dom to be fully constructed
                        setTimeout(() => waitForContainer(retries - 1), 50);
                    } else {
                        reject(new Error(`Container element with ID '${finalConfig.containerId}' not found after multiple retries`));
                    }
                };

                waitForContainer();
            });
        },

        // Get player options based on stream type - Configures Video.js options for live vs VOD
        getPlayerOptions: function(isLiveStream, autoplay) {
            const playerOptions = {
                fluid: true,
                responsive: true,
                autoplay: autoplay,
                liveui: isLiveStream,
                fill: true,
                controlBar: {
                    pictureInPictureToggle: false,
                    // Different control bar layouts for live vs VOD
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

            // Add live-specific options for better live stream handling
            if (isLiveStream) {
                playerOptions.liveTracker = {
                    trackingThreshold: 20,
                    liveTolerance: 15
                };
                playerOptions.html5 = {
                    vhs: {
                        overrideNative: !this.IS_SAFARI // Use native HLS on Safari, VHS on others
                    }
                };
            }

            return playerOptions;
        },

        // Setup comprehensive error handling - Provides detailed error messages for debugging
        setupErrorHandling: function(player, container) {
            player.on('error', function() {
                const error = player.error();
                console.error('Video.js error:', error);

                let errorMessage = 'An error occurred while loading the video.';
                const currentVideoUrl = player.currentSrc();
                errorMessage += ` | Video URL: ${currentVideoUrl}`;

                // Map Video.js error codes to human-readable messages
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

                    // Append the stringified error object for debugging
                    try {
                        errorMessage += ' | Error details: ' + JSON.stringify(error);
                    } catch (e) {
                        errorMessage += ' | Error details (fallback): ' + String(error);
                    }
                }

                container.innerHTML = `<div class="error-message">${errorMessage}</div>`;
            });
        },

        // Build video URL based on player type - Constructs appropriate URLs for different CDN providers
        buildVideoUrl: function(playerType, clientId, hlsId) {
            if (!playerType || !['bunny', 'castr'].includes(playerType)) {
                throw new Error('Invalid player_type. Must be "bunny" or "castr".');
            }

            if (playerType === 'bunny') {
                // Bunny CDN - typically for VOD content
                return {
                    url: `https://${clientId}.b-cdn.net/${hlsId}/playlist.m3u8`,
                    isLive: false
                };
            } else if (playerType === 'castr') {
                // Castr - for live streaming with DVR capability (1800 seconds = 30 minutes)
                return {
                    url: `https://stream.castr.net/${clientId}/live_${hlsId}/rewind-1800.m3u8`,
                    isLive: true
                };
            }
        },

        // Set menu to click-only (no hover) - Prevents accidental menu opening on hover
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

            // Toggle menu on click only
            btn.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                // Toggle menu open/close state
                btn.classList.toggle('vjs-menu-button-popup-active');
                // Find the menu and toggle its visibility
                var menu = btn.querySelector('.vjs-menu');
                if (menu) {
                    menu.style.display = (menu.style.display === 'block') ? '' : 'block';
                }
            });
        },

        // DVR functionality for live streams - Checks if stream has enough seekable content for DVR
        checkDVRCapability: function(player, isLiveStream) {
            if (!player || !isLiveStream) return false;

            const seekable = player.seekable();

            if (seekable && seekable.length > 0) {
                const dvrWindow = seekable.end(0) - seekable.start(0);
                const hasDVR = dvrWindow > this.DVR_THRESHOLD;

                // Add/remove DVR class based on availability
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

        // Update live status indicator - Updates UI to show if viewer is at live edge or behind
        updateLiveStatus: function(player, isLiveStream) {
            if (!player || !isLiveStream) return;

            const seekable = player.seekable();
            const currentTime = player.currentTime();

            if (seekable && seekable.length > 0) {
                const liveEdge = seekable.end(0);
                const timeBehindLive = liveEdge - currentTime;
                const isAtLiveEdge = timeBehindLive < 10; // Within 10 seconds of live

                // Update live button appearance based on position
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

        // Setup DVR functionality with periodic checks
        setupDVRFunctionality: function(player, isLiveStream) {
            const self = this;
            // Check DVR capability every 2 seconds
            setInterval(() => self.checkDVRCapability(player, isLiveStream), 2000);
            // Update live status on every time update
            player.on('timeupdate', () => self.updateLiveStatus(player, isLiveStream));
        },

        // Update HD tag display - Shows/hides HD indicator based on current quality
        updateHDTag: function(selectedQuality) {
            const button = document.querySelector('.vjs-quality-selector-hls');
            if (!button) return;

            const isHD = selectedQuality && selectedQuality.height && selectedQuality.height >= 720;
            let existingTag = button.querySelector('.vjs-quality-hd-tag');

            if (isHD && !existingTag) {
                // Add HD tag for high quality streams
                const hdTag = document.createElement('span');
                hdTag.className = 'vjs-quality-hd-tag';
                hdTag.textContent = 'HD';
                button.querySelector('.vjs-menu-button').appendChild(hdTag);
            } else if (!isHD && existingTag) {
                // Remove HD tag for lower quality streams
                existingTag.remove();
            }
        },

        // Setup quality selector with HD indicator
        setupQualitySelector: function(player) {
            // Quality selector is not supported in Safari (uses native HLS)
            if (!this.IS_SAFARI && player.qualityLevels && typeof player.qualityLevels === 'function') {
                const qualityLevels = player.qualityLevels();
                const self = this;

                qualityLevels.on('addqualitylevel', function(event) {
                    console.log('Quality level added:', event.qualityLevel);
                });

                // Initialize HLS quality selector plugin
                player.qualitySelectorHls({
                    displayCurrentQuality: true,
                    vjsIconClass: 'vjs-icon-hd'
                });

                // Update HD tag when quality changes
                qualityLevels.on('change', function() {
                    const selectedQuality = qualityLevels[qualityLevels.selectedIndex];
                    self.updateHDTag(selectedQuality);
                });

                // Update HD tag when metadata loads
                player.on('loadedmetadata', () => {
                    if (qualityLevels && qualityLevels.length > 0 && qualityLevels.selectedIndex !== -1) {
                        self.updateHDTag(qualityLevels[qualityLevels.selectedIndex]);
                    }
                });
            } else {
                console.warn('HLS quality selector not supported in this browser.');
            }
        },

        // Setup audio tracks - CRITICAL FUNCTION FOR TRACK LABELING
        // This function implements the core track labeling logic:
        // 1. If customLabels are provided (from tracksParam), use those
        // 2. If no customLabels, fall back to video metadata language codes and map them to display names
        setupAudioTracks: function(player, customLabels) {
            const self = this;

            // Wait for metadata to load before processing audio tracks
            player.on('loadedmetadata', function() {
                try {
                    const audioTracks = player.audioTracks();

                    if (audioTracks && audioTracks.tracks_) {
                        const languageMap = self.languageMap;

                        // PRIORITY 1: Use custom labels if provided via tracksParam
                        if (customLabels.length > 0) {
                            // Apply custom labels from tracksParam in order
                            audioTracks.tracks_.forEach((track, index) => {
                                if (customLabels[index]) {
                                    track.label = customLabels[index];
                                }
                            });
                        } else {
                            // PRIORITY 2: Fall back to video metadata language codes
                            // Map language codes from video metadata to human-readable names
                            audioTracks.tracks_.forEach((track) => {
                                const lang = track.language; // This comes from video metadata
                                if (languageMap[lang]) {
                                    track.label = languageMap[lang];
                                }
                                // If language code not in map, original label/language is preserved
                            });
                        }

                        // Update the audio track button UI with new labels
                        if (player.controlBar.audioTrackButton) {
                            player.controlBar.audioTrackButton.update();
                        }
                    }
                } catch (error) {
                    console.warn('Audio tracks setup failed:', error);
                }
            });
        },

        // Parse custom labels - Processes tracksParam to determine track labels
        // This function determines whether tracksParam contains language codes or display names
        parseCustomLabels: function(tracksParam) {
           if (!tracksParam || tracksParam === 'undefined') {
               return [];
           }
            const languageMap = this.languageMap;
            try {
                const trackItems = tracksParam.split(',').map(code => code.trim());

                // Heuristic: If all items are keys in languageMap, assume they're language codes
                const isCodeList = trackItems.every(code => languageMap[code]);

                if (isCodeList) {
                    // Convert language codes to display labels using languageMap
                    return trackItems.map(code => languageMap[code]);
                } else {
                    // Items are already display labels, use as-is
                    return trackItems;
                }
            } catch (error) {
                console.warn('Error parsing tracksParam, using default labels:', error);
                return [];
            }
        },

        // Dispose current player if exists - Clean up to prevent memory leaks
        disposeCurrentPlayer: function() {
            if (this.currentPlayer) {
                try {
                    this.currentPlayer.dispose();
                } catch (error) {
                    console.warn('Error disposing player:', error);
                }
                this.currentPlayer = null;
            }
        },

        // Player control methods - Public API for controlling playback
        play: function() {
            if (this.currentPlayer) {
                this.currentPlayer.play();
            }
        },

        pause: function() {
            if (this.currentPlayer) {
                this.currentPlayer.pause();
            }
        },

        stop: function() {
            if (this.currentPlayer) {
                // Video.js doesn't have a direct stop, so we pause and seek to beginning
                this.currentPlayer.pause();
                this.currentPlayer.currentTime(0);
            }
        },

        requestFullscreen: function() {
            if (this.currentPlayer) {
                this.currentPlayer.requestFullscreen();
            }
        },

        exitFullscreen: function() {
            if (this.currentPlayer) {
                this.currentPlayer.exitFullscreen();
            }
        }
    };

    console.log('VideoPlayerManager initialized');
})();

// Proper pausePlayer function with callback support
// Provides robust pause functionality with error handling and callback support
function pausePlayer(callback) {
    console.log('pausePlayer called');

    // Check if VideoPlayerManager exists
    if (!window.VideoPlayerManager) {
        console.error('VideoPlayerManager not found');
        if (callback) callback(false, 'VideoPlayerManager not found');
        return;
    }

    // Check if there's a current player instance
    if (!window.VideoPlayerManager.currentPlayer) {
        console.warn('No current player found');
        if (callback) callback(false, 'No current player found');
        return;
    }

    const player = window.VideoPlayerManager.currentPlayer;

    try {
        // Check if player is ready before attempting to pause
        if (player.readyState() === 0) {
            console.warn('Player not ready yet, waiting...');
            player.ready(() => {
                try {
                    player.pause();
                    console.log('Player paused after ready');
                    if (callback) callback(true, 'Player paused successfully');
                } catch (error) {
                    console.error('Error pausing player after ready:', error);
                    if (callback) callback(false, 'Error pausing player: ' + error.message);
                }
            });
        } else {
            // Player is ready, pause immediately
            player.pause();
            console.log('Player paused immediately');
            if (callback) callback(true, 'Player paused successfully');
        }
    } catch (error) {
        console.error('Error in pausePlayer:', error);
        if (callback) callback(false, 'Error pausing player: ' + error.message);
    }
}

// Alternative pausePlayer function that returns a Promise
// Modern Promise-based API for pause functionality
function pausePlayerAsync() {
    return new Promise((resolve, reject) => {
        pausePlayer((success, message) => {
            if (success) {
                resolve(message);
            } else {
                reject(new Error(message));
            }
        });
    });
}

// Video Player Dynamic Loading - Simple function call with minimal parameters
// Legacy wrapper function for backward compatibility
function loadVideo(playerType, hlsId, clientId, tracksParam, playerId) {
    // Check if VideoPlayerManager is available
    if (!window.VideoPlayerManager) {
        throw new Error('VideoPlayerManager not found. Please load the static initialization script first.');
    }

    // Call the static manager and get the promise
    const playerPromise = window.VideoPlayerManager.loadVideo({
        playerType: playerType,
        hlsId: hlsId,
        clientId: clientId,
        tracksParam: tracksParam,
        javaPlayerId: playerId
    });

    // Handle the promise to get the actual player object
    playerPromise.then(player => {
        // Add event listeners to notify Java side (for embedded applications)
        if (player) {
            const javaPlayer = window[playerId]; // Correctly reference the javaPlayer using the passed playerId
            if (javaPlayer) {
                player.ready(function() {
                    javaPlayer.onReady();
                });
                player.on('play', function() { javaPlayer.onPlay(); });
                player.on('pause', function() { javaPlayer.onPause(); });
                player.on('ended', function() { javaPlayer.onEnd(); });
            }
        }
    }).catch(error => {
        console.error("Failed to load video in legacy loadVideo function:", error);
    });

    // Return the promise for any downstream code that might use it
    return playerPromise;
}

// Alternative function with object parameter for backward compatibility
function loadVideoWithConfig(config) {
    console.log('enter loadVideoWithConfig');
    if (!window.VideoPlayerManager) {
        throw new Error('VideoPlayerManager not found. Please load the static initialization script first.');
    }

    return window.VideoPlayerManager.loadVideo(config);
}

// Safe initialization function - Ensures all dependencies are loaded before execution
function safeOnLoad(callback) {
    console.log('enter safeOnLoad');
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
    console.log('leave safeOnLoad');
}

