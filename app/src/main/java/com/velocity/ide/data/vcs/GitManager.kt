package com.velocity.ide.data.vcs

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

data class GitStatusResult(
    val changedFiles: List<String>,
    val untrackedFiles: List<String>,
    val missingFiles: List<String>,
    val isClean: Boolean
)

class GitManager {

    /**
     * Initializes a new Git repository in the specified directory.
     */
    fun initRepo(directory: File): Boolean {
        return try {
            Git.init().setDirectory(directory).call().use { git ->
                // Ensure initial commit to make the branch valid
                git.commit().setMessage("Initial commit").setSign(false).call()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Gets the current status of the working tree.
     */
    fun getStatus(directory: File): GitStatusResult {
        return try {
            Git.open(directory).use { git ->
                val status = git.status().call()
                GitStatusResult(
                    changedFiles = status.changed.toList() + status.modified.toList(),
                    untrackedFiles = status.untracked.toList(),
                    missingFiles = status.missing.toList(),
                    isClean = status.isClean
                )
            }
        } catch (e: Exception) {
            // Not a git repo or other error
            GitStatusResult(emptyList(), emptyList(), emptyList(), isClean = true)
        }
    }

    /**
     * Stages all changes and commits them.
     */
    fun commitAll(directory: File, message: String, authorName: String, authorEmail: String): Boolean {
        return try {
            Git.open(directory).use { git ->
                // Add all files
                git.add().addFilepattern(".").call()
                
                // Add removed files
                git.add().setUpdate(true).addFilepattern(".").call()
                
                // Commit
                val ident = PersonIdent(authorName, authorEmail)
                git.commit()
                    .setMessage(message)
                    .setAuthor(ident)
                    .setCommitter(ident)
                    .call()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Pushes to origin main (or master).
     */
    fun push(directory: File, token: String, remoteUrl: String? = null): Boolean {
        return try {
            Git.open(directory).use { git ->
                val pushCommand = git.push()
                    .setCredentialsProvider(UsernamePasswordCredentialsProvider("TOKEN", token))
                
                if (remoteUrl != null) {
                    pushCommand.setRemote(remoteUrl)
                }
                
                pushCommand.call()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Pulls from origin.
     */
    fun pull(directory: File, token: String): Boolean {
        return try {
            Git.open(directory).use { git ->
                git.pull()
                    .setCredentialsProvider(UsernamePasswordCredentialsProvider("TOKEN", token))
                    .call()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Checks if directory is a git repo.
     */
    fun isGitRepo(directory: File): Boolean {
        val gitDir = File(directory, ".git")
        return gitDir.exists() && gitDir.isDirectory
    }
}
